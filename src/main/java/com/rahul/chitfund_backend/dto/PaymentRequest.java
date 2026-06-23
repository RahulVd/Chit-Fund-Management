package com.rahul.chitfund_backend.dto;

import com.rahul.chitfund_backend.entity.PaymentMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentRequest {

    @NotNull(message = "Chit group ID is required")
    private Long chitGroupId;

    @NotNull(message = "Member ID is required")
    private Long memberId;

    @NotNull(message = "Month number is required")
    @Min(value = 1, message = "Month number must be at least 1")
    private Integer monthNumber;

    @NotNull(message = "Amount paid is required")
    @DecimalMin(value = "1.0", message = "Amount must be greater than 0")
    private BigDecimal amountPaid;

    private LocalDate paymentDate;   // optional — null means "use today" server-side

    private PaymentMode paymentMode; // optional — CASH, UPI, BANK_TRANSFER, OTHER

    private String referenceNote;    // optional — free text
}