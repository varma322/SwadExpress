package com.fooddelivery.restaurant.controller;

import com.fooddelivery.restaurant.dto.MenuItemDto;
import com.fooddelivery.restaurant.dto.RestaurantDto;
import com.fooddelivery.restaurant.service.RestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public ResponseEntity<List<RestaurantDto>> searchRestaurants(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) Long userId) {
        List<RestaurantDto> results = restaurantService.searchRestaurants(query, cuisine, userId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDto> getRestaurant(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        RestaurantDto restaurant = restaurantService.getRestaurantById(id, userId);
        return ResponseEntity.ok(restaurant);
    }

    @GetMapping("/{id}/menu")
    public ResponseEntity<List<MenuItemDto>> getRestaurantMenu(@PathVariable Long id) {
        List<MenuItemDto> menu = restaurantService.getRestaurantMenu(id);
        return ResponseEntity.ok(menu);
    }

    @GetMapping("/cuisines")
    public ResponseEntity<List<String>> getCuisines() {
        return ResponseEntity.ok(restaurantService.getCuisines());
    }
}
