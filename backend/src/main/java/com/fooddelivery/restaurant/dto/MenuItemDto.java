package com.fooddelivery.restaurant.dto;

import java.math.BigDecimal;

public class MenuItemDto {

    private Long id;
    private Long restaurantId;
    private String name;
    private String category;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private boolean isVegetarian;
    private boolean isSpicy;
    private boolean isAvailable;

    public MenuItemDto() {}

    public MenuItemDto(Long id, Long restaurantId, String name, String category, String description,
                       BigDecimal price, String imageUrl, boolean isVegetarian, boolean isSpicy, boolean isAvailable) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isVegetarian = isVegetarian;
        this.isSpicy = isSpicy;
        this.isAvailable = isAvailable;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isVegetarian() { return isVegetarian; }
    public void setVegetarian(boolean vegetarian) { isVegetarian = vegetarian; }

    public boolean isSpicy() { return isSpicy; }
    public void setSpicy(boolean spicy) { isSpicy = spicy; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}
