package com.fooddelivery.review.dto;

import java.time.LocalDateTime;

public class ReviewResponseDto {

    private Long id;
    private Long orderId;
    private Long userId;
    private String userName;
    private Long restaurantId;
    private Integer rating;
    private String comments;
    private LocalDateTime createdAt;

    public ReviewResponseDto() {}

    public ReviewResponseDto(Long id, Long orderId, Long userId, String userName, Long restaurantId,
                             Integer rating, String comments, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.restaurantId = restaurantId;
        this.rating = rating;
        this.comments = comments;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
