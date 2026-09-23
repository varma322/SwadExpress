package com.fooddelivery.restaurant.controller;

import com.fooddelivery.restaurant.dto.FavoriteResponseDto;
import com.fooddelivery.restaurant.dto.FavoriteToggleRequestDto;
import com.fooddelivery.restaurant.dto.RestaurantDto;
import com.fooddelivery.restaurant.service.FavoriteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/toggle")
    public ResponseEntity<FavoriteResponseDto> toggleFavorite(@Valid @RequestBody FavoriteToggleRequestDto request) {
        FavoriteResponseDto response = favoriteService.toggleFavorite(request.getUserId(), request.getRestaurantId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RestaurantDto>> getUserFavorites(@PathVariable Long userId) {
        List<RestaurantDto> favorites = favoriteService.getUserFavorites(userId);
        return ResponseEntity.ok(favorites);
    }
}
