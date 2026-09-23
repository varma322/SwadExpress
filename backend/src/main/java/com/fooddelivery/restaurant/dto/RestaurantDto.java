package com.fooddelivery.restaurant.dto;

import java.math.BigDecimal;

public class RestaurantDto {

    private Long id;
    private String name;
    private String cuisine;
    private Double rating;
    private Integer reviewCount;
    private Integer deliveryTimeMinutes;
    private BigDecimal deliveryFee;
    private BigDecimal minOrderAmount;
    private String imageUrl;
    private String address;
    private boolean isOpen;
    private boolean isFeatured;
    private boolean isFavorite;

    public RestaurantDto() {}

    public RestaurantDto(Long id, String name, String cuisine, Double rating, Integer reviewCount,
                         Integer deliveryTimeMinutes, BigDecimal deliveryFee, BigDecimal minOrderAmount,
                         String imageUrl, String address, boolean isOpen, boolean isFeatured, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.cuisine = cuisine;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.deliveryTimeMinutes = deliveryTimeMinutes;
        this.deliveryFee = deliveryFee;
        this.minOrderAmount = minOrderAmount;
        this.imageUrl = imageUrl;
        this.address = address;
        this.isOpen = isOpen;
        this.isFeatured = isFeatured;
        this.isFavorite = isFavorite;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public Integer getDeliveryTimeMinutes() { return deliveryTimeMinutes; }
    public void setDeliveryTimeMinutes(Integer deliveryTimeMinutes) { this.deliveryTimeMinutes = deliveryTimeMinutes; }

    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(BigDecimal deliveryFee) { this.deliveryFee = deliveryFee; }

    public BigDecimal getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(BigDecimal minOrderAmount) { this.minOrderAmount = minOrderAmount; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public boolean isOpen() { return isOpen; }
    public void setOpen(boolean open) { isOpen = open; }

    public boolean isFeatured() { return isFeatured; }
    public void setFeatured(boolean featured) { isFeatured = featured; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}
