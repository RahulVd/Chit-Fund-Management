package com.rahul.chitfund_backend.service;

import com.rahul.chitfund_backend.dto.MonthlyPaymentSummary;
import com.rahul.chitfund_backend.entity.ChitGroup;
import com.rahul.chitfund_backend.entity.Member;
import com.rahul.chitfund_backend.entity.Payment;
import com.rahul.chitfund_backend.entity.PaymentStatus;
import com.rahul.chitfund_backend.exception.DuplicatePaymentException;
import com.rahul.chitfund_backend.exception.ResourceNotFoundException;
import com.rahul.chitfund_backend.repository.ChitGroupRepository;
import com.rahul.chitfund_backend.repository.MemberRepository;
import com.rahul.chitfund_backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ChitGroupRepository chitGroupRepository;

    @Autowired
    private MemberRepository memberRepository;

    public Payment recordPayment(Long chitGroupId, Long memberId,
                                 Integer monthNumber, BigDecimal amount) {

        // 1. Check duplicate
        Optional<Payment> existing = paymentRepository
                .findByChitGroupIdAndMemberIdAndMonthNumber(chitGroupId, memberId, monthNumber);

        if (existing.isPresent()) {
            throw new DuplicatePaymentException("Payment already recorded for this member and month.");
        }

        // 2. Fetch group and member
        ChitGroup group = chitGroupRepository.findById(chitGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("Chit group not found"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        // 3. Build and save payment
        Payment payment = new Payment();
        payment.setChitGroup(group);
        payment.setMember(member);
        payment.setMonthNumber(monthNumber);
        payment.setAmountPaid(amount);
        payment.setPaymentDate(LocalDate.now());
        payment.setStatus(PaymentStatus.PAID);

        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByGroup(Long chitGroupId) {
        return paymentRepository.findByChitGroupId(chitGroupId);
    }

    public List<Member> getMembersWhoHaveNotPaid(Long chitGroupId, Integer monthNumber) {
        return paymentRepository.findMembersWhoHaveNotPaid(chitGroupId, monthNumber);
    }

    public MonthlyPaymentSummary getMonthlySummary(Long chitGroupId, Integer monthNumber) {

        ChitGroup group = chitGroupRepository.findById(chitGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("Chit group not found"));

        List<Payment> payments = paymentRepository
                .findByChitGroupIdAndMonthNumber(chitGroupId, monthNumber);

        int totalMembers = group.getTotalMembers();
        int paidCount = payments.size();
        BigDecimal totalCollected = payments.stream()
                .map(Payment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpected = group.getMonthlyContribution()
                .multiply(BigDecimal.valueOf(totalMembers));

        return new MonthlyPaymentSummary(monthNumber, totalMembers, paidCount,
                totalExpected, totalCollected);
    }
}