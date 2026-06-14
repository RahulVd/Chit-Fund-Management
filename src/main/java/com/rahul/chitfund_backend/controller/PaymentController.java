package com.rahul.chitfund_backend.controller;

import com.rahul.chitfund_backend.dto.MonthlyPaymentSummary;
import com.rahul.chitfund_backend.dto.PaymentRequest;
import com.rahul.chitfund_backend.entity.Member;
import com.rahul.chitfund_backend.entity.Payment;
import com.rahul.chitfund_backend.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/record")
    public ResponseEntity<Payment> recordPayment(@Valid @RequestBody PaymentRequest request) {
        Payment payment = paymentService.recordPayment(
                request.getChitGroupId(),
                request.getMemberId(),
                request.getMonthNumber(),
                request.getAmountPaid()
        );
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/group/{chitGroupId}")
    public ResponseEntity<List<Payment>> getGroupPayments(@PathVariable Long chitGroupId) {
        return ResponseEntity.ok(paymentService.getPaymentsByGroup(chitGroupId));
    }

    @GetMapping("/group/{chitGroupId}/month/{monthNumber}/unpaid")
    public ResponseEntity<List<Member>> getUnpaidMembers(@PathVariable Long chitGroupId,
                                                         @PathVariable Integer monthNumber) {
        return ResponseEntity.ok(paymentService.getMembersWhoHaveNotPaid(chitGroupId, monthNumber));
    }

    @GetMapping("/group/{chitGroupId}/month/{monthNumber}/summary")
    public ResponseEntity<  MonthlyPaymentSummary> getMonthlySummary(@PathVariable Long chitGroupId,
                                                                   @PathVariable Integer monthNumber) {
        return ResponseEntity.ok(paymentService.getMonthlySummary(chitGroupId, monthNumber));
    }
}
