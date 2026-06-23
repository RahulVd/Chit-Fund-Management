package com.rahul.chitfund_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "owner_payments")
public class OwnerPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chit_group_id", nullable = false)
    private ChitGroup chitGroup;

    @Column(nullable = false)
    private Integer monthNumber;   // same meaning as Payment.monthNumber

    @Column(nullable = false)
    private BigDecimal amountPaid;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;   // reuses your existing PaymentStatus enum
}