package com.rahul.chitfund_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MeetingRequest {

    @NotNull(message = "Chit group ID is required")
    private Long chitGroupId;

    @NotNull(message = "Month number is required")
    @Min(value = 1, message = "Month number must be at least 1")
    private Integer monthNumber;

    @NotBlank(message = "Meeting link is required")
    private String meetLink;

    private String notes;

}
