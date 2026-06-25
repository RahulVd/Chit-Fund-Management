package com.rahul.chitfund_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AuctionRequest {

    @NotNull(message = "Chit group ID is required")
    private Long chitGroupId;

    @NotNull(message = "Winner ID is required")
    private Long winnerId;

    @NotNull(message = "Month number is required")
    @Min(value = 1, message = "Month number must be at least 1")
    private Integer monthNumber;

    @NotNull(message = "Bid amount is required")
    @DecimalMin(value = "1.0", message = "Bid amount must be greater than 0")
    private BigDecimal bidAmount;

    private boolean isDoubleChit;

}
