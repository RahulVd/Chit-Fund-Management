package com.rahul.chitfund_backend.controller;

import com.rahul.chitfund_backend.entity.OwnerPayment;
import com.rahul.chitfund_backend.service.OwnerPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner-payments")
public class OwnerPaymentController {

    @Autowired
    private OwnerPaymentService ownerPaymentService;

    @PostMapping("/group/{chitGroupId}/month/{monthNumber}")
    public ResponseEntity<OwnerPayment> recordOwnerPayment(@PathVariable Long chitGroupId,
                                                           @PathVariable Integer monthNumber) {
        OwnerPayment payment = ownerPaymentService.recordOwnerPayment(chitGroupId, monthNumber);
        return ResponseEntity.ok(payment);
    }

    @DeleteMapping("/group/{chitGroupId}/month/{monthNumber}")
    public ResponseEntity<Void> unmarkOwnerPayment(@PathVariable Long chitGroupId,
                                                   @PathVariable Integer monthNumber) {
        ownerPaymentService.unmarkOwnerPayment(chitGroupId, monthNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/group/{chitGroupId}")
    public ResponseEntity<List<OwnerPayment>> getOwnerPayments(@PathVariable Long chitGroupId) {
        return ResponseEntity.ok(ownerPaymentService.getOwnerPaymentsByGroup(chitGroupId));
    }
}