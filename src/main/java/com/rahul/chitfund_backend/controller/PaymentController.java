package com.rahul.chitfund_backend.controller;

import com.rahul.chitfund_backend.dto.MemberPaymentStatus;
import com.rahul.chitfund_backend.dto.MonthlyPaymentSummary;
import com.rahul.chitfund_backend.dto.PaymentRequest;
import com.rahul.chitfund_backend.entity.Member;
import com.rahul.chitfund_backend.entity.Payment;
import com.rahul.chitfund_backend.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                request.getPaymentDate(),
                request.getPaymentMode(),
                request.getReferenceNote()
        );
        return ResponseEntity.ok(payment);
    }

    @DeleteMapping("/group/{chitGroupId}/month/{monthNumber}/member/{memberId}")
    public ResponseEntity<Void> unmarkPayment(@PathVariable Long chitGroupId,
                                              @PathVariable Integer monthNumber,
                                              @PathVariable Long memberId) {
        paymentService.unmarkPayment(chitGroupId, memberId, monthNumber);
        return ResponseEntity.noContent().build();
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

    @GetMapping("/group/{chitGroupId}/month/{monthNumber}/status")
    public ResponseEntity<List<MemberPaymentStatus>> getMonthStatus(@PathVariable Long chitGroupId,
                                                                    @PathVariable Integer monthNumber) {
        return ResponseEntity.ok(paymentService.getMonthStatus(chitGroupId, monthNumber));
    }

    @GetMapping("/group/{chitGroupId}/month/{monthNumber}/summary")
    public ResponseEntity<MonthlyPaymentSummary> getMonthlySummary(@PathVariable Long chitGroupId,
                                                                   @PathVariable Integer monthNumber) {
        return ResponseEntity.ok(paymentService.getMonthlySummary(chitGroupId, monthNumber));
    }
}