package com.fooddelivery.support.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateTicketDto {

    @NotNull(message = "User ID is required")
    private Long userId;

    private Long orderId;

    @NotBlank(message = "Category is required (e.g. LATE_DELIVERY, MISSING_ITEM, FOOD_QUALITY, REFUND, OTHER)")
    private String category;

    @NotBlank(message = "Problem description is required")
    private String description;

    public CreateTicketDto() {}

    public CreateTicketDto(Long userId, Long orderId, String category, String description) {
        this.userId = userId;
        this.orderId = orderId;
        this.category = category;
        this.description = description;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
