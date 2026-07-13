package com.rahul.chitfund_backend.service;

import com.rahul.chitfund_backend.entity.ChitGroup;
import com.rahul.chitfund_backend.entity.ChitGroupStatus;
import com.rahul.chitfund_backend.entity.Member;
import com.rahul.chitfund_backend.entity.Settlement;
import com.rahul.chitfund_backend.exception.CustomException;
import com.rahul.chitfund_backend.repository.ChitGroupRepository;
import com.rahul.chitfund_backend.repository.MemberRepository;
import com.rahul.chitfund_backend.repository.SettlementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

    @Mock private ChitGroupRepository chitGroupRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private SettlementRepository settlementRepository;

    private SettlementService service;
    private static final Long GROUP_ID = 1L;

    @BeforeEach
    void setUp() {
        service = new SettlementService(chitGroupRepository, memberRepository, settlementRepository);
    }

    private ChitGroup completedGroup(BigDecimal balance) {
        ChitGroup g = new ChitGroup();
        g.setId(GROUP_ID);
        g.setStatus(ChitGroupStatus.COMPLETED);
        g.setChitGroupBalance(balance);
        g.setTotalMembers(3);
        return g;
    }

    private List<Member> members(int count) {
        List<Member> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Member m = new Member();
            m.setId((long) i + 1);
            list.add(m);
        }
        return list;
    }

    @Test
    void dividendSplitsExactly_1000_dividedBy_3() {
        ChitGroup g = completedGroup(new BigDecimal("1000.00"));
        List<Member> ms = members(3);

        when(chitGroupRepository.findById(GROUP_ID)).thenReturn(Optional.of(g));
        when(settlementRepository.existsByChitGroupId(GROUP_ID)).thenReturn(false);
        when(memberRepository.findByChitGroupId(GROUP_ID)).thenReturn(ms);
        when(settlementRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<Settlement> settlements = service.settleGroup(GROUP_ID);

        BigDecimal total = settlements.stream()
                .map(Settlement::getDividendAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, new BigDecimal("1000.00").compareTo(total));
    }

    @Test
    void dividendSplitsExactly_100_dividedBy_3() {
        ChitGroup g = completedGroup(new BigDecimal("100.00"));
        List<Member> ms = members(3);

        when(chitGroupRepository.findById(GROUP_ID)).thenReturn(Optional.of(g));
        when(settlementRepository.existsByChitGroupId(GROUP_ID)).thenReturn(false);
        when(memberRepository.findByChitGroupId(GROUP_ID)).thenReturn(ms);
        when(settlementRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<Settlement> settlements = service.settleGroup(GROUP_ID);

        BigDecimal total = settlements.stream()
                .map(Settlement::getDividendAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, new BigDecimal("100.00").compareTo(total));
    }

    @Test
    void dividendSplitsExactly_99999_dividedBy_7() {
        ChitGroup g = completedGroup(new BigDecimal("999.99"));
        List<Member> ms = members(7);

        when(chitGroupRepository.findById(GROUP_ID)).thenReturn(Optional.of(g));
        when(settlementRepository.existsByChitGroupId(GROUP_ID)).thenReturn(false);
        when(memberRepository.findByChitGroupId(GROUP_ID)).thenReturn(ms);
        when(settlementRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<Settlement> settlements = service.settleGroup(GROUP_ID);

        BigDecimal total = settlements.stream()
                .map(Settlement::getDividendAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(0, new BigDecimal("999.99").compareTo(total));
    }

    @Test
    void settleTwice_throwsException() {
        ChitGroup g = completedGroup(new BigDecimal("1000"));
        when(chitGroupRepository.findById(GROUP_ID)).thenReturn(Optional.of(g));
        when(settlementRepository.existsByChitGroupId(GROUP_ID)).thenReturn(true);

        assertThrows(CustomException.class, () -> service.settleGroup(GROUP_ID));
    }

    @Test
    void nonCompletedGroup_throwsException() {
        ChitGroup g = new ChitGroup();
        g.setId(GROUP_ID);
        g.setStatus(ChitGroupStatus.ACTIVE);
        when(chitGroupRepository.findById(GROUP_ID)).thenReturn(Optional.of(g));

        assertThrows(CustomException.class, () -> service.settleGroup(GROUP_ID));
    }
}
