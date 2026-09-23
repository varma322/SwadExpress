package com.fooddelivery.restaurant.service;

import com.fooddelivery.common.exception.ResourceNotFoundException;
import com.fooddelivery.restaurant.dto.FavoriteResponseDto;
import com.fooddelivery.restaurant.dto.RestaurantDto;
import com.fooddelivery.restaurant.entity.Favorite;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.repository.FavoriteRepository;
import com.fooddelivery.restaurant.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final RestaurantRepository restaurantRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, RestaurantRepository restaurantRepository) {
        this.favoriteRepository = favoriteRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public FavoriteResponseDto toggleFavorite(Long userId, Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        Optional<Favorite> existing = favoriteRepository.findByUserIdAndRestaurantId(userId, restaurantId);
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return new FavoriteResponseDto(userId, restaurantId, false, "Removed " + restaurant.getName() + " from favorites.");
        } else {
            Favorite favorite = new Favorite(userId, restaurantId);
            favoriteRepository.save(favorite);
            return new FavoriteResponseDto(userId, restaurantId, true, "Added " + restaurant.getName() + " to favorites.");
        }
    }

    @Transactional(readOnly = true)
    public List<RestaurantDto> getUserFavorites(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        List<Long> restaurantIds = favorites.stream().map(Favorite::getRestaurantId).collect(Collectors.toList());

        return restaurantRepository.findAllById(restaurantIds).stream()
                .map(r -> new RestaurantDto(
                        r.getId(),
                        r.getName(),
                        r.getCuisine(),
                        r.getRating(),
                        r.getReviewCount(),
                        r.getDeliveryTimeMinutes(),
                        r.getDeliveryFee(),
                        r.getMinOrderAmount(),
                        r.getImageUrl(),
                        r.getAddress(),
                        r.isOpen(),
                        r.isFeatured(),
                        true
                ))
                .collect(Collectors.toList());
    }
}
