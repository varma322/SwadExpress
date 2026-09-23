package com.fooddelivery;

import com.fooddelivery.order.dto.CreateOrderRequestDto;
import com.fooddelivery.order.dto.OrderItemRequestDto;
import com.fooddelivery.order.dto.OrderResponseDto;
import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.service.OrderService;
import com.fooddelivery.restaurant.dto.FavoriteResponseDto;
import com.fooddelivery.restaurant.dto.MenuItemDto;
import com.fooddelivery.restaurant.dto.RestaurantDto;
import com.fooddelivery.restaurant.service.FavoriteService;
import com.fooddelivery.restaurant.service.RestaurantService;
import com.fooddelivery.review.dto.CreateReviewDto;
import com.fooddelivery.review.dto.ReviewResponseDto;
import com.fooddelivery.review.service.ReviewService;
import com.fooddelivery.support.dto.CreateTicketDto;
import com.fooddelivery.support.dto.TicketResponseDto;
import com.fooddelivery.support.service.SupportService;
import com.fooddelivery.tracking.dto.TrackingResponseDto;
import com.fooddelivery.tracking.service.TrackingService;
import com.fooddelivery.user.dto.AddressDto;
import com.fooddelivery.user.dto.UserLoginDto;
import com.fooddelivery.user.dto.UserProfileDto;
import com.fooddelivery.user.dto.UserRegistrationDto;
import com.fooddelivery.user.service.AddressService;
import com.fooddelivery.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FoodDeliveryBackendApplicationTests {

    @Autowired private UserService userService;
    @Autowired private AddressService addressService;
    @Autowired private RestaurantService restaurantService;
    @Autowired private FavoriteService favoriteService;
    @Autowired private OrderService orderService;
    @Autowired private TrackingService trackingService;
    @Autowired private ReviewService reviewService;
    @Autowired private SupportService supportService;

    @Test
    void contextLoads() {
        assertNotNull(userService);
        assertNotNull(restaurantService);
        assertNotNull(orderService);
    }

    @Test
    @DisplayName("UC-1: User Registration & Login")
    void testUserRegistrationAndLogin() {
        UserRegistrationDto regDto = new UserRegistrationDto("Jane Doe", "jane.doe@example.com", "+1 555-901-2345", "passwordJane");
        UserProfileDto created = userService.register(regDto);
        assertNotNull(created.getId());
        assertEquals("Jane Doe", created.getName());
        assertEquals("jane.doe@example.com", created.getEmail());

        UserLoginDto loginDto = new UserLoginDto("jane.doe@example.com", "passwordJane");
        UserProfileDto loggedIn = userService.login(loginDto);
        assertEquals(created.getId(), loggedIn.getId());
    }

    @Test
    @DisplayName("UC-2: Restaurant Search & Filtering")
    void testRestaurantSearch() {
        List<RestaurantDto> northIndian = restaurantService.searchRestaurants(null, "North Indian", 1L);
        assertFalse(northIndian.isEmpty());
        assertTrue(northIndian.stream().allMatch(r -> r.getCuisine().equalsIgnoreCase("North Indian")));

        List<RestaurantDto> searchByQuery = restaurantService.searchRestaurants("Biryani", null, 1L);
        assertFalse(searchByQuery.isEmpty());
        assertTrue(searchByQuery.stream().anyMatch(r -> r.getName().contains("Biryani")));
    }

    @Test
    @DisplayName("UC-3: View Menu Items & Categories")
    void testViewMenu() {
        List<RestaurantDto> restaurants = restaurantService.searchRestaurants(null, null, 1L);
        assertFalse(restaurants.isEmpty());
        Long restaurantId = restaurants.get(0).getId();

        List<MenuItemDto> menu = restaurantService.getRestaurantMenu(restaurantId);
        assertFalse(menu.isEmpty());
        assertTrue(menu.stream().allMatch(m -> m.getRestaurantId().equals(restaurantId)));
    }

    @Test
    @DisplayName("UC-4 & UC-5: Place Order & Track Telemetry")
    void testPlaceOrderAndTrack() {
        List<RestaurantDto> restaurants = restaurantService.searchRestaurants(null, "North Indian", 1L);
        Long restaurantId = restaurants.get(0).getId();
        List<MenuItemDto> menu = restaurantService.getRestaurantMenu(restaurantId);
        MenuItemDto item = menu.get(0);

        CreateOrderRequestDto orderReq = new CreateOrderRequestDto(
                1L,
                restaurantId,
                List.of(new OrderItemRequestDto(item.getId(), 2)),
                "Flat 402, Shanti Niketan, Indiranagar, Bengaluru",
                "Leave near gate",
                "UPI"
        );

        OrderResponseDto order = orderService.createOrder(orderReq);
        assertNotNull(order.getId());
        assertEquals(OrderStatus.PLACED, order.getStatus());
        assertEquals("SUCCESS", order.getPaymentStatus());

        TrackingResponseDto tracking = trackingService.getTracking(order.getId());
        assertNotNull(tracking);
        assertEquals(1, tracking.getCurrentStep());

        TrackingResponseDto step2 = trackingService.advanceStep(order.getId());
        assertEquals(2, step2.getCurrentStep());
        assertEquals(OrderStatus.PREPARING, step2.getStatus());
    }

    @Test
    @DisplayName("UC-6: Review and Rate Completed Order")
    void testReviewAndRate() {
        // Find existing completed order seeded for user 1
        List<OrderResponseDto> orders = orderService.getOrdersByUser(1L);
        OrderResponseDto deliveredOrder = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .findFirst()
                .orElse(null);

        assertNotNull(deliveredOrder);
        // Seeded order is already reviewed, let's verify reviews for that restaurant
        List<ReviewResponseDto> reviews = reviewService.getReviewsByRestaurant(deliveredOrder.getRestaurantId());
        assertFalse(reviews.isEmpty());
        assertTrue(reviews.stream().anyMatch(r -> r.getOrderId().equals(deliveredOrder.getId())));
    }

    @Test
    @DisplayName("UC-7: Manage Account Profile")
    void testManageAccount() {
        UserProfileDto profile = userService.getProfile(1L);
        assertEquals("Aarav Sharma", profile.getName());

        profile.setName("Aarav V. Sharma");
        UserProfileDto updated = userService.updateProfile(1L, profile);
        assertEquals("Aarav V. Sharma", updated.getName());
    }

    @Test
    @DisplayName("UC-8: Save & Toggle Favorite Restaurants")
    void testFavorites() {
        FavoriteResponseDto res = favoriteService.toggleFavorite(1L, 3L);
        assertNotNull(res);

        List<RestaurantDto> favorites = favoriteService.getUserFavorites(1L);
        assertNotNull(favorites);

        FavoriteResponseDto untoggle = favoriteService.toggleFavorite(1L, 3L);
        assertNotNull(untoggle);
    }

    @Test
    @DisplayName("UC-9: Delivery Address Book")
    void testAddressManagement() {
        AddressDto newAddr = new AddressDto(null, 1L, "Gym", "80ft Road, Koramangala", "4th Block", "Bengaluru", "Karnataka", "560034", false);
        AddressDto saved = addressService.addAddress(1L, newAddr);
        assertNotNull(saved.getId());

        List<AddressDto> list = addressService.getAddressesByUserId(1L);
        assertTrue(list.stream().anyMatch(a -> a.getId().equals(saved.getId())));

        addressService.deleteAddress(1L, saved.getId());
    }

    @Test
    @DisplayName("UC-10: Customer Support Tickets")
    void testSupportTickets() {
        CreateTicketDto ticketDto = new CreateTicketDto(1L, null, "GENERAL", "Do you offer catering services for office events?");
        TicketResponseDto created = supportService.createTicket(ticketDto);
        assertNotNull(created.getTicketNumber());
        assertEquals("OPEN", created.getStatus());

        List<TicketResponseDto> tickets = supportService.getUserTickets(1L);
        assertTrue(tickets.stream().anyMatch(t -> t.getTicketNumber().equals(created.getTicketNumber())));
    }
}
