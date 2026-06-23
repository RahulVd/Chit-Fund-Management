package com.rahul.chitfund_backend.service;

import com.rahul.chitfund_backend.entity.ChitGroup;
import com.rahul.chitfund_backend.entity.OwnerPayment;
import com.rahul.chitfund_backend.entity.PaymentStatus;
import com.rahul.chitfund_backend.exception.DuplicatePaymentException;
import com.rahul.chitfund_backend.exception.ResourceNotFoundException;
import com.rahul.chitfund_backend.repository.ChitGroupRepository;
import com.rahul.chitfund_backend.repository.OwnerPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OwnerPaymentService {

    @Autowired
    private OwnerPaymentRepository ownerPaymentRepository;

    @Autowired
    private ChitGroupRepository chitGroupRepository;

    @Transactional
    public OwnerPayment recordOwnerPayment(Long chitGroupId, Integer monthNumber) {

        Optional<OwnerPayment> existing = ownerPaymentRepository
                .findByChitGroupIdAndMonthNumber(chitGroupId, monthNumber);

        if (existing.isPresent()) {
            throw new DuplicatePaymentException("Owner payment already recorded for this month.");
        }

        ChitGroup group = chitGroupRepository.findById(chitGroupId)
                .orElseThrow(() -> new ResourceNotFoundException("Chit group not found"));

        OwnerPayment payment = new OwnerPayment();
        payment.setChitGroup(group);
        payment.setMonthNumber(monthNumber);
        payment.setAmountPaid(group.getMonthlyContribution()); // fixed, server-side, same as member payments
        payment.setPaymentDate(LocalDate.now());
        payment.setStatus(PaymentStatus.PAID);

        return ownerPaymentRepository.save(payment);
    }

    @Transactional
    public void unmarkOwnerPayment(Long chitGroupId, Integer monthNumber) {
        OwnerPayment payment = ownerPaymentRepository
                .findByChitGroupIdAndMonthNumber(chitGroupId, monthNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Owner payment not found"));
        ownerPaymentRepository.delete(payment);
    }

    public List<OwnerPayment> getOwnerPaymentsByGroup(Long chitGroupId) {
        return ownerPaymentRepository.findByChitGroupId(chitGroupId);
    }
}