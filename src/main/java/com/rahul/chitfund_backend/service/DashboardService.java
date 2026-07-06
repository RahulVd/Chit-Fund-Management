package com.rahul.chitfund_backend.service;

import com.rahul.chitfund_backend.dto.DashboardSummary;
import com.rahul.chitfund_backend.entity.Auction;
import com.rahul.chitfund_backend.entity.ChitGroup;
import com.rahul.chitfund_backend.entity.Member;
import com.rahul.chitfund_backend.entity.OwnerMonth;
import com.rahul.chitfund_backend.entity.OwnerPayment;
import com.rahul.chitfund_backend.entity.Payment;
import com.rahul.chitfund_backend.exception.CustomException;
import com.rahul.chitfund_backend.repository.AuctionRepository;
import com.rahul.chitfund_backend.repository.ChitGroupRepository;
import com.rahul.chitfund_backend.repository.OwnerMonthRepository;
import com.rahul.chitfund_backend.repository.OwnerPaymentRepository;
import com.rahul.chitfund_backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private ChitGroupRepository chitGroupRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private OwnerPaymentRepository ownerPaymentRepository;

    @Autowired
    private OwnerMonthRepository ownerMonthRepository;

    public DashboardSummary getGroupDashboard(Long chitGroupId) {
        ChitGroup group = chitGroupRepository.findById(chitGroupId)
                .orElseThrow(() -> new CustomException("Chit group not found"));

        List<Payment> allPayments = paymentRepository.findByChitGroupId(chitGroupId);
        BigDecimal memberTotal = allPayments.stream()
                .map(Payment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<OwnerPayment> ownerPayments = ownerPaymentRepository.findByChitGroupId(chitGroupId);
        BigDecimal ownerTotal = ownerPayments.stream()
                .map(OwnerPayment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCollected = memberTotal.add(ownerTotal);

        // Current month = highest month with a completed Auction or OwnerMonth, + 1.
        // Same rule used across AuctionService, PaymentService, and CloseMonth —
        // NEVER derive this from payment data, since payments can be recorded
        // ahead of an actual auction/owner-month happening.
        int maxAuctionMonth = auctionRepository.findByChitGroupId(chitGroupId)
                .stream()
                .mapToInt(Auction::getMonthNumber)
                .max()
                .orElse(0);

        int maxOwnerMonth = ownerMonthRepository.findByChitGroupId(chitGroupId)
                .stream()
                .mapToInt(OwnerMonth::getMonthNumber)
                .max()
                .orElse(0);

        int currentMonth = Math.max(maxAuctionMonth, maxOwnerMonth) + 1;

        List<Auction> auctions = auctionRepository.findByChitGroupId(chitGroupId);
        int membersWhoWon = auctions.size();

        // Unpaid members for the CURRENT month
        List<Member> unpaidMembers = paymentRepository.findMembersWhoHaveNotPaid(chitGroupId, currentMonth);
        List<String> unpaidNames = unpaidMembers.stream()
                .map(Member::getName)
                .collect(Collectors.toList());

        return new DashboardSummary(
                group.getChitName(),
                group.getTotalMembers(),
                group.getMonthlyContribution(),
                group.getTotalChitAmount(),
                currentMonth,
                totalCollected,
                membersWhoWon,
                unpaidNames
        );
    }
}