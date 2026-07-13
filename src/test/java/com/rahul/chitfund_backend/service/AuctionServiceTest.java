package com.rahul.chitfund_backend.service;

import com.rahul.chitfund_backend.entity.Auction;
import com.rahul.chitfund_backend.entity.ChitGroup;
import com.rahul.chitfund_backend.entity.ChitGroupStatus;
import com.rahul.chitfund_backend.entity.Member;
import com.rahul.chitfund_backend.exception.CustomException;
import com.rahul.chitfund_backend.repository.AuctionRepository;
import com.rahul.chitfund_backend.repository.ChitGroupRepository;
import com.rahul.chitfund_backend.repository.MemberRepository;
import com.rahul.chitfund_backend.repository.OwnerMonthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuctionServiceTest {

    @Mock private AuctionRepository auctionRepository;
    @Mock private ChitGroupRepository chitGroupRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private OwnerMonthRepository ownerMonthRepository;

    private AuctionService auctionService;

    private static final Long GROUP_ID = 1L;
    private static final BigDecimal MONTHLY = new BigDecimal("10000");
    private static final BigDecimal TOTAL_CHIT = new BigDecimal("100000");
    private static final int MEMBER_COUNT = 10;
    private static final Long WINNER_ID = 5L;

    private ChitGroup group;
    private Member winner;

    @BeforeEach
    void setUp() {
        auctionService = new AuctionService();
        injectMock(auctionService, "auctionRepository", auctionRepository);
        injectMock(auctionService, "chitGroupRepository", chitGroupRepository);
        injectMock(auctionService, "memberRepository", memberRepository);
        injectMock(auctionService, "ownerMonthRepository", ownerMonthRepository);

        group = new ChitGroup();
        group.setId(GROUP_ID);
        group.setTotalMembers(MEMBER_COUNT);
        group.setMonthlyContribution(MONTHLY);
        group.setTotalChitAmount(TOTAL_CHIT);
        group.setChitGroupBalance(BigDecimal.ZERO);
        group.setStatus(ChitGroupStatus.ACTIVE);

        winner = new Member();
        winner.setId(WINNER_ID);
        winner.setName("Winner");

        when(chitGroupRepository.findById(GROUP_ID)).thenReturn(Optional.of(group));
        when(memberRepository.findById(WINNER_ID)).thenReturn(Optional.of(winner));
        when(auctionRepository.findByChitGroupId(GROUP_ID)).thenReturn(Collections.emptyList());
        when(ownerMonthRepository.findByChitGroupId(GROUP_ID)).thenReturn(Collections.emptyList());
        when(memberRepository.countByChitGroupId(GROUP_ID)).thenReturn((long) MEMBER_COUNT);
    }

    @Test
    void normalAuction_addsBidToBalance() {
        BigDecimal bid = new BigDecimal("25000");
        when(auctionRepository.findByChitGroupIdAndMonthNumber(GROUP_ID, 1)).thenReturn(Collections.emptyList());
        when(auctionRepository.findByChitGroupIdAndWinnerId(GROUP_ID, WINNER_ID)).thenReturn(Optional.empty());
        when(auctionRepository.save(any())).thenAnswer(inv -> { Auction a = inv.getArgument(0); a.setId(100L); return a; });

        Auction result = auctionService.recordAuction(GROUP_ID, WINNER_ID, 1, bid, false);

        assertEquals(new BigDecimal("75000"), result.getReceivedAmount());
        assertEquals(0, new BigDecimal("25000").compareTo(result.getChitGroupBalanceAfter()));
        assertEquals(0, new BigDecimal("25000").compareTo(group.getChitGroupBalance()));
    }

    @Test
    void duplicateWinner_throwsException() {
        when(auctionRepository.findByChitGroupIdAndMonthNumber(GROUP_ID, 1)).thenReturn(Collections.emptyList());
        when(auctionRepository.findByChitGroupIdAndWinnerId(GROUP_ID, WINNER_ID)).thenReturn(Optional.of(new Auction()));

        assertThrows(CustomException.class,
                () -> auctionService.recordAuction(GROUP_ID, WINNER_ID, 1, new BigDecimal("25000"), false));
    }

    @Test
    void bidExceedsMax_throwsException() {
        when(auctionRepository.findByChitGroupIdAndMonthNumber(GROUP_ID, 1)).thenReturn(Collections.emptyList());
        when(auctionRepository.findByChitGroupIdAndWinnerId(GROUP_ID, WINNER_ID)).thenReturn(Optional.empty());

        assertThrows(CustomException.class,
                () -> auctionService.recordAuction(GROUP_ID, WINNER_ID, 1, new BigDecimal("55000"), false));
    }

    @Test
    void doubleChit_correctBalance() {
        group.setChitGroupBalance(new BigDecimal("80000"));

        // Month 1 + month 2 auctions exist (so month 3 is allowed)
        Auction a1 = new Auction();
        a1.setMonthNumber(1);
        Auction a2 = new Auction();
        a2.setMonthNumber(2);
        when(auctionRepository.findByChitGroupId(GROUP_ID)).thenReturn(List.of(a1, a2));
        // First auction of month 3 exists (second will be the double chit)
        Auction existingMonth3 = new Auction();
        existingMonth3.setMonthNumber(3);
        when(auctionRepository.findByChitGroupIdAndMonthNumber(GROUP_ID, 3)).thenReturn(List.of(existingMonth3));
        when(auctionRepository.findByChitGroupIdAndWinnerId(GROUP_ID, WINNER_ID)).thenReturn(Optional.empty());
        when(auctionRepository.save(any())).thenAnswer(inv -> { Auction a = inv.getArgument(0); a.setId(102L); return a; });

        BigDecimal bid = new BigDecimal("20000");
        Auction result = auctionService.recordAuction(GROUP_ID, WINNER_ID, 3, bid, true);

        // balance = 80000 - 100000 + 20000 = 0
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getChitGroupBalanceAfter()));
        assertEquals(0, BigDecimal.ZERO.compareTo(group.getChitGroupBalance()));
    }

    @Test
    void doubleChitBlocked_whenBalanceAt60Percent() {
        group.setChitGroupBalance(new BigDecimal("60000"));
        Auction first = new Auction();
        first.setMonthNumber(3);
        when(auctionRepository.findByChitGroupIdAndMonthNumber(GROUP_ID, 3)).thenReturn(List.of(first));
        when(auctionRepository.findByChitGroupIdAndWinnerId(GROUP_ID, WINNER_ID)).thenReturn(Optional.empty());

        assertThrows(CustomException.class,
                () -> auctionService.recordAuction(GROUP_ID, WINNER_ID, 3, new BigDecimal("20000"), true));
    }

    @Test
    void doubleChitBlocked_whenNoFirstAuction() {
        when(auctionRepository.findByChitGroupIdAndMonthNumber(GROUP_ID, 1)).thenReturn(Collections.emptyList());
        when(auctionRepository.findByChitGroupIdAndWinnerId(GROUP_ID, WINNER_ID)).thenReturn(Optional.empty());

        assertThrows(CustomException.class,
                () -> auctionService.recordAuction(GROUP_ID, WINNER_ID, 1, new BigDecimal("20000"), true));
    }

    @Test
    void completedGroup_throwsException() {
        group.setStatus(ChitGroupStatus.COMPLETED);
        assertThrows(CustomException.class,
                () -> auctionService.recordAuction(GROUP_ID, WINNER_ID, 1, new BigDecimal("25000"), false));
    }

    @Test
    void recordAuction_hasTransactionalAnnotation() throws Exception {
        assertTrue(AuctionService.class.getDeclaredMethod("recordAuction",
                Long.class, Long.class, Integer.class, BigDecimal.class, boolean.class)
                .isAnnotationPresent(Transactional.class));
    }

    @Test
    void getChitGroupBalance_returnsField() {
        group.setChitGroupBalance(new BigDecimal("12345.67"));
        assertEquals(0, new BigDecimal("12345.67").compareTo(auctionService.getChitGroupBalance(GROUP_ID)));
    }

    @Test
    void getLastMonthPayout_usesNewKeyName() {
        group.setChitGroupBalance(new BigDecimal("50000"));
        Map<String, Object> payout = auctionService.getLastMonthPayout(GROUP_ID);
        assertTrue(payout.containsKey("chitGroupBalance"));
        assertFalse(payout.containsKey("ownerBalance"));
    }

    private void injectMock(Object target, String fieldName, Object mock) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
