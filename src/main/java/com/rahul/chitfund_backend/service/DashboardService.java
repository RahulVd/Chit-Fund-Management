package com.rahul.chitfund_backend.service;

import com.rahul.chitfund_backend.dto.DashboardSummary;
import com.rahul.chitfund_backend.entity.Auction;
import com.rahul.chitfund_backend.entity.ChitGroup;
import com.rahul.chitfund_backend.entity.Member;
import com.rahul.chitfund_backend.entity.OwnerPayment;
import com.rahul.chitfund_backend.entity.Payment;
import com.rahul.chitfund_backend.exception.CustomException;
import com.rahul.chitfund_backend.repository.AuctionRepository;
import com.rahul.chitfund_backend.repository.ChitGroupRepository;
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

        // Find the latest month that has ANY auction recorded — that's the true current month
        int currentMonth = auctionRepository.findByChitGroupId(chitGroupId)
                .stream()
                .mapToInt(Auction::getMonthNumber)
                .max()
                .orElse(0);

// If no auctions yet, fall back to highest payment month
        if (currentMonth == 0) {
            currentMonth = allPayments.stream()
                    .mapToInt(Payment::getMonthNumber)
                    .max()
                    .orElse(0);
        }

        List<Auction> auctions = auctionRepository.findByChitGroupId(chitGroupId);
        int membersWhoWon = auctions.size();

        // Unpaid members for the CURRENT month — empty list if no payments recorded yet at all
        List<String> unpaidNames;
        if (currentMonth > 0) {
            List<Member> unpaidMembers = paymentRepository.findMembersWhoHaveNotPaid(chitGroupId, currentMonth);
            unpaidNames = unpaidMembers.stream()
                    .map(Member::getName)
                    .collect(Collectors.toList());
        } else {
            unpaidNames = Collections.emptyList();
        }

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