package com.fooddelivery.review.service;

import com.fooddelivery.common.exception.BadRequestException;
import com.fooddelivery.common.exception.ResourceNotFoundException;
import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.repository.OrderRepository;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.repository.RestaurantRepository;
import com.fooddelivery.review.dto.CreateReviewDto;
import com.fooddelivery.review.dto.ReviewResponseDto;
import com.fooddelivery.review.entity.Review;
import com.fooddelivery.review.repository.ReviewRepository;
import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         OrderRepository orderRepository,
                         RestaurantRepository restaurantRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    public ReviewResponseDto createReview(CreateReviewDto dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + dto.getOrderId()));

        if (!order.getUserId().equals(dto.getUserId())) {
            throw new BadRequestException("Order does not belong to user id: " + dto.getUserId());
        }

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BadRequestException("Reviews can only be submitted for completed/delivered orders.");
        }

        if (reviewRepository.existsByOrderId(dto.getOrderId())) {
            throw new BadRequestException("You have already reviewed this order.");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        Restaurant restaurant = restaurantRepository.findById(order.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + order.getRestaurantId()));

        Review review = new Review(
                order.getId(),
                user.getId(),
                user.getName(),
                restaurant.getId(),
                dto.getRating(),
                dto.getComments() != null ? dto.getComments().trim() : ""
        );

        Review savedReview = reviewRepository.save(review);

        // Update restaurant rating and review count
        List<Review> allReviews = reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurant.getId());
        double avg = allReviews.stream().mapToInt(Review::getRating).average().orElse(dto.getRating());
        double roundedAvg = BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP).doubleValue();

        restaurant.setRating(roundedAvg);
        restaurant.setReviewCount(allReviews.size());
        restaurantRepository.save(restaurant);

        return toDto(savedReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByRestaurant(Long restaurantId) {
        return reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ReviewResponseDto> getReviewByOrder(Long orderId) {
        return reviewRepository.findByOrderId(orderId).map(this::toDto);
    }

    private ReviewResponseDto toDto(Review r) {
        return new ReviewResponseDto(
                r.getId(),
                r.getOrderId(),
                r.getUserId(),
                r.getUserName(),
                r.getRestaurantId(),
                r.getRating(),
                r.getComments(),
                r.getCreatedAt()
        );
    }
}
