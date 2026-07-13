package com.rahul.chitfund_backend.service;

import com.rahul.chitfund_backend.dto.DashboardSummary;
import com.rahul.chitfund_backend.entity.*;
import com.rahul.chitfund_backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DashboardServiceTest {

    @Mock private ChitGroupRepository chitGroupRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private AuctionRepository auctionRepository;
    @Mock private OwnerPaymentRepository ownerPaymentRepository;
    @Mock private OwnerMonthRepository ownerMonthRepository;

    private DashboardService service;
    private static final Long GROUP_ID = 1L;

    @BeforeEach
    void setUp() {
        service = new DashboardService();
        injectMock(service, "chitGroupRepository", chitGroupRepository);
        injectMock(service, "paymentRepository", paymentRepository);
        injectMock(service, "auctionRepository", auctionRepository);
        injectMock(service, "ownerPaymentRepository", ownerPaymentRepository);
        injectMock(service, "ownerMonthRepository", ownerMonthRepository);
    }

    private ChitGroup makeGroup(int totalMembers, ChitGroupStatus status) {
        ChitGroup g = new ChitGroup();
        g.setId(GROUP_ID);
        g.setChitName("Test");
        g.setTotalMembers(totalMembers);
        g.setMonthlyContribution(new BigDecimal("10000"));
        g.setTotalChitAmount(new BigDecimal("100000"));
        g.setChitGroupBalance(BigDecimal.ZERO);
        g.setStatus(status);
        return g;
    }

    private List<Auction> auctionsForMonths(int... months) {
        List<Auction> list = new ArrayList<>();
        for (int m : months) {
            Auction a = new Auction();
            a.setMonthNumber(m);
            list.add(a);
        }
        return list;
    }

    @Test
    void completedGroup_clampsCurrentMonth() {
        ChitGroup g = makeGroup(10, ChitGroupStatus.COMPLETED);

        when(chitGroupRepository.findById(GROUP_ID)).thenReturn(Optional.of(g));
        when(paymentRepository.findByChitGroupId(GROUP_ID)).thenReturn(Collections.emptyList());
        when(ownerPaymentRepository.findByChitGroupId(GROUP_ID)).thenReturn(Collections.emptyList());
        when(auctionRepository.findByChitGroupId(GROUP_ID)).thenReturn(auctionsForMonths(1,2,3,4,5,6,7,8,9,10));
        when(ownerMonthRepository.findByChitGroupId(GROUP_ID)).thenReturn(Collections.emptyList());

        DashboardSummary summary = service.getGroupDashboard(GROUP_ID);

        assertEquals(10, summary.getCurrentMonth(), "Completed group should show currentMonth=totalMembers, not N+1");
        assertTrue(summary.getUnpaidMemberNamesThisMonth().isEmpty(), "Completed group should have empty unpaid list");
    }

    @Test
    void activeGroup_showsNextMonth() {
        ChitGroup g = makeGroup(10, ChitGroupStatus.ACTIVE);

        when(chitGroupRepository.findById(GROUP_ID)).thenReturn(Optional.of(g));
        when(paymentRepository.findByChitGroupId(GROUP_ID)).thenReturn(Collections.emptyList());
        when(ownerPaymentRepository.findByChitGroupId(GROUP_ID)).thenReturn(Collections.emptyList());
        when(auctionRepository.findByChitGroupId(GROUP_ID)).thenReturn(auctionsForMonths(1,2,3,4,5));
        when(ownerMonthRepository.findByChitGroupId(GROUP_ID)).thenReturn(Collections.emptyList());
        when(paymentRepository.findMembersWhoHaveNotPaid(GROUP_ID, 6)).thenReturn(Collections.emptyList());

        DashboardSummary summary = service.getGroupDashboard(GROUP_ID);

        assertEquals(6, summary.getCurrentMonth(), "Active group after 5 months should show month 6");
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
