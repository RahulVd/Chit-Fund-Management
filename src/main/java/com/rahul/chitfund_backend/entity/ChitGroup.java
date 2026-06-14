package com.rahul.chitfund_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
public class ChitGroup {




    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chitName;

    private Integer totalMembers;

    private BigDecimal monthlyContribution;

    private BigDecimal totalChitAmount;

    private LocalDate startDate;

    private BigDecimal ownerBalance = BigDecimal.ZERO;



    @Enumerated(EnumType.STRING)
    private ChitGroupStatus status;
}