package com.rahul.chitfund_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MemberPaymentStatus {

    private Long memberId;
    private String memberName;
    private boolean paid;
    private BigDecimal amountPaid;
    private LocalDate paymentDate;

    public MemberPaymentStatus(Long memberId, String memberName, boolean paid,
                               BigDecimal amountPaid, LocalDate paymentDate) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.paid = paid;
        this.amountPaid = amountPaid;
        this.paymentDate = paymentDate;
    }

    public Long getMemberId() { return memberId; }
    public String getMemberName() { return memberName; }
    public boolean isPaid() { return paid; }
    public BigDecimal getAmountPaid() { return amountPaid; }
    public LocalDate getPaymentDate() { return paymentDate; }
}