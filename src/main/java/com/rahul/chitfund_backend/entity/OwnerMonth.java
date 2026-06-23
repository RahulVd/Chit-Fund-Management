package com.rahul.chitfund_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

    @Data
    @Entity
    @Table(name = "owner_months")
    public class OwnerMonth {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "chit_group_id", nullable = false)
        private ChitGroup chitGroup;

        @Column(nullable = false)
        private Integer monthNumber;

        @Column(nullable = false)
        private BigDecimal amountAdded;

        @Column(nullable = false)
        private LocalDate triggeredDate;
    }

