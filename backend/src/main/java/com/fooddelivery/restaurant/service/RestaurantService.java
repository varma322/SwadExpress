package com.fooddelivery.restaurant.service;

import com.fooddelivery.common.exception.ResourceNotFoundException;
import com.fooddelivery.restaurant.dto.MenuItemDto;
import com.fooddelivery.restaurant.dto.RestaurantDto;
import com.fooddelivery.restaurant.entity.MenuItem;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.repository.FavoriteRepository;
import com.fooddelivery.restaurant.repository.MenuItemRepository;
import com.fooddelivery.restaurant.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final FavoriteRepository favoriteRepository;

    public RestaurantService(RestaurantRepository restaurantRepository,
                             MenuItemRepository menuItemRepository,
                             FavoriteRepository favoriteRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.favoriteRepository = favoriteRepository;
    }

    public List<RestaurantDto> searchRestaurants(String query, String cuisine, Long userId) {
        String cleanQuery = (query != null && !query.trim().isBlank()) ? query.trim() : null;
        String cleanCuisine = (cuisine != null && !cuisine.trim().equalsIgnoreCase("all") && !cuisine.trim().isBlank()) ? cuisine.trim() : null;

        List<Restaurant> restaurants = restaurantRepository.searchRestaurants(cleanQuery, cleanCuisine);

        Set<Long> favoriteRestaurantIds = Collections.emptySet();
        if (userId != null) {
            favoriteRestaurantIds = favoriteRepository.findByUserId(userId)
                    .stream()
                    .map(f -> f.getRestaurantId())
                    .collect(Collectors.toSet());
        }

        final Set<Long> favs = favoriteRestaurantIds;
        return restaurants.stream()
                .map(r -> toDto(r, favs.contains(r.getId())))
                .collect(Collectors.toList());
    }

    public RestaurantDto getRestaurantById(Long id, Long userId) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));

        boolean isFav = false;
        if (userId != null) {
            isFav = favoriteRepository.existsByUserIdAndRestaurantId(userId, id);
        }
        return toDto(restaurant, isFav);
    }

    public List<MenuItemDto> getRestaurantMenu(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + restaurantId);
        }

        return menuItemRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(this::toMenuItemDto)
                .collect(Collectors.toList());
    }

    public List<String> getCuisines() {
        return restaurantRepository.findDistinctCuisines();
    }

    private RestaurantDto toDto(Restaurant r, boolean isFavorite) {
        return new RestaurantDto(
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
                isFavorite
        );
    }

    private MenuItemDto toMenuItemDto(MenuItem m) {
        return new MenuItemDto(
                m.getId(),
                m.getRestaurantId(),
                m.getName(),
                m.getCategory(),
                m.getDescription(),
                m.getPrice(),
                m.getImageUrl(),
                m.isVegetarian(),
                m.isSpicy(),
                m.isAvailable()
        );
    }
}
