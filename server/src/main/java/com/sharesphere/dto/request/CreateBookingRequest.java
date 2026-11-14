package com.sharesphere.dto.request;

import com.sharesphere.entity.FulfillmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateBookingRequest {

    @NotBlank(message = "Item ID is required.")
    private String itemId;

    @NotNull(message = "Start date is required.")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required.")
    private LocalDateTime endDate;

    private FulfillmentType fulfillmentType = FulfillmentType.SELF_PICKUP;
}
