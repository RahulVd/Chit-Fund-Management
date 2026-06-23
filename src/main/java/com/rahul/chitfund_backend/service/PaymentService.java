package com.rahul.chitfund_backend.service;

import com.rahul.chitfund_backend.dto.MemberPaymentStatus;
import com.rahul.chitfund_backend.dto.MonthlyPaymentSummary;
import com.rahul.chitfund_backend.entity.*;
import com.rahul.chitfund_backend.exception.DuplicatePaymentException;
import com.rahul.chitfund_backend.exception.ResourceNotFoundException;
import com.rahul.chitfund_backend.repository.ChitGroupRepository;
import com.rahul.chitfund_backend.repository.MemberRepository;
import com.rahul.chitfund_backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ChitGroupRepository chitGroupRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Transactional
    public Payment recordPayment(Long chitGroupId, Long memberId, Integer monthNumber,
                                 LocalDate paymentDate, PaymentMode paymentMode, String referenceNote) {

        Optional<Payment> existing = paymentRepository
                .findByChitGroupIdAndMemberIdAndMonthNumber(chitGroupId, memberId, monthNumber);

        if (existing.isPresent()) {
            throw new DuplicatePaymentException("Payment already recorded for this member and month.");
        }

        ChitGroup group = chitGroupRepository.findById(chitGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("Chit group not found"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        Payment payment = new Payment();
        payment.setChitGroup(group);
        payment.setMember(member);
        payment.setMonthNumber(monthNumber);
        payment.setAmountPaid(group.getMonthlyContribution());
        payment.setPaymentDate(paymentDate != null ? paymentDate : LocalDate.now());
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentMode(paymentMode);
        payment.setReferenceNote(referenceNote);

        return paymentRepository.save(payment);
    }

    @Transactional
    public void unmarkPayment(Long chitGroupId, Long memberId, Integer monthNumber) {
        Payment payment = paymentRepository
                .findByChitGroupIdAndMemberIdAndMonthNumber(chitGroupId, memberId, monthNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        paymentRepository.delete(payment);
    }

    public List<Payment> getPaymentsByGroup(Long chitGroupId) {
        return paymentRepository.findByChitGroupId(chitGroupId);
    }

    public List<Member> getMembersWhoHaveNotPaid(Long chitGroupId, Integer monthNumber) {
        return paymentRepository.findMembersWhoHaveNotPaid(chitGroupId, monthNumber);
    }

    public List<MemberPaymentStatus> getMonthStatus(Long chitGroupId, Integer monthNumber) {

        ChitGroup group = chitGroupRepository.findById(chitGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("Chit group not found"));

        List<Member> members = memberRepository.findByChitGroupId(chitGroupId);

        List<Payment> payments = paymentRepository
                .findByChitGroupIdAndMonthNumber(chitGroupId, monthNumber);

        Map<Long, Payment> paymentByMember = payments.stream()
                .collect(Collectors.toMap(p -> p.getMember().getId(), p -> p));

        return members.stream()
                .map(m -> {
                    Payment p = paymentByMember.get(m.getId());
                    if (p != null) {
                        return new MemberPaymentStatus(m.getId(), m.getName(), true,
                                p.getAmountPaid(), p.getPaymentDate());
                    } else {
                        return new MemberPaymentStatus(m.getId(), m.getName(), false, null, null);
                    }
                })
                .collect(Collectors.toList());
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