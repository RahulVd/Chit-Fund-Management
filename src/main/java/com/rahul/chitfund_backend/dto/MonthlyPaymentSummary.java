package com.rahul.chitfund_backend.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MonthlyPaymentSummary {
    private Integer monthNumber;
    private int totalMembers;
    private int paidCount;
    private int unpaidCount;
    private BigDecimal totalExpected;
    private BigDecimal totalCollected;

    // constructor
    public MonthlyPaymentSummary(Integer monthNumber, int totalMembers, int paidCount,
                                 BigDecimal totalExpected, BigDecimal totalCollected) {
        this.monthNumber = monthNumber;
        this.totalMembers = totalMembers;
        this.paidCount = paidCount;
        this.unpaidCount = totalMembers - paidCount;
        this.totalExpected = totalExpected;
        this.totalCollected = totalCollected;
    }

    // getters
}