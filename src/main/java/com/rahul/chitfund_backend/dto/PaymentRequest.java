package com.rahul.chitfund_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

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
}
