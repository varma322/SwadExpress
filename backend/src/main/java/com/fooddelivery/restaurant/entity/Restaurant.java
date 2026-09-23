package com.fooddelivery.restaurant.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String cuisine; // Italian, Mexican, Burgers, Indian, Japanese, Healthy

    private Double rating = 4.5;

    private Integer reviewCount = 0;

    private Integer deliveryTimeMinutes = 30;

    private BigDecimal deliveryFee = BigDecimal.valueOf(2.99);

    private BigDecimal minOrderAmount = BigDecimal.valueOf(15.00);

    private String imageUrl;

    private String address;

    private boolean isOpen = true;

    private boolean isFeatured = false;

    public Restaurant() {}

    public Restaurant(String name, String cuisine, Double rating, Integer reviewCount, Integer deliveryTimeMinutes,
                      BigDecimal deliveryFee, BigDecimal minOrderAmount, String imageUrl, String address, boolean isOpen, boolean isFeatured) {
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
}
