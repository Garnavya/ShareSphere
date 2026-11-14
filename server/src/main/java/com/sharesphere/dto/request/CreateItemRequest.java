package com.sharesphere.dto.request;

import com.sharesphere.entity.ItemCondition;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateItemRequest {

    @NotBlank(message = "Title is required.")
    private String title;

    private String description;

    @NotBlank(message = "Category is required.")
    private String category;

    private ItemCondition condition = ItemCondition.GOOD;
    private BigDecimal dailyRate = BigDecimal.ZERO;
    private BigDecimal securityDeposit = BigDecimal.ZERO;
    private Boolean isFree = false;

    @NotBlank(message = "Address is required.")
    private String address;

    private Double latitude;
    private Double longitude;
    private List<String> imageUrls;
}
