package com.fooddelivery.restaurant.dto;

public class FavoriteResponseDto {

    private Long userId;
    private Long restaurantId;
    private boolean isFavorite;
    private String message;

    public FavoriteResponseDto() {}

    public FavoriteResponseDto(Long userId, Long restaurantId, boolean isFavorite, String message) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.isFavorite = isFavorite;
        this.message = message;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
