package com.fooddelivery.common.docs;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenAPI 3.0 Specification Endpoint (Capstone NFR 5.8 Usability & API Documentation)
 * Provides comprehensive documentation for all 10 Microservice Use Cases.
 */
@RestController
public class OpenApiController {

    @GetMapping(value = "/v3/api-docs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getOpenApiDocumentation() {
        Map<String, Object> spec = new LinkedHashMap<>();
        spec.put("openapi", "3.0.3");

        Map<String, Object> info = new LinkedHashMap<>();
        info.put("title", "SwadExpress - Food Delivery Microservices API");
        info.put("description", "Comprehensive RESTful API for Food Delivery Capstone Project fulfilling all 10 Functional Use Cases and NFRs (Spring Boot, Actuator, Swagger/OpenAPI, JWT, H2/MySQL).");
        info.put("version", "1.0.0");
        spec.put("info", info);

        spec.put("servers", List.of(Map.of("url", "http://localhost:8085", "description", "Local Development API Gateway")));

        Map<String, Object> paths = new LinkedHashMap<>();

        // UC-1 & UC-7: Users
        paths.put("/api/v1/users/register", Map.of("post", Map.of(
                "tags", List.of("UC-1: User Registration"),
                "summary", "Register a new customer account",
                "responses", Map.of("201", Map.of("description", "User registered successfully"), "409", Map.of("description", "Email already exists"))
        )));
        paths.put("/api/v1/users/login", Map.of("post", Map.of(
                "tags", List.of("UC-1: User Registration & Auth"),
                "summary", "Authenticate user and issue JWT token",
                "responses", Map.of("200", Map.of("description", "Authentication successful with JWT token"), "401", Map.of("description", "Invalid credentials"))
        )));
        paths.put("/api/v1/users/{id}", Map.of(
                "get", Map.of("tags", List.of("UC-7: Manage Account"), "summary", "Retrieve user profile details"),
                "put", Map.of("tags", List.of("UC-7: Manage Account"), "summary", "Update user profile details")
        ));

        // UC-2 & UC-3: Restaurants & Menu
        paths.put("/api/v1/restaurants", Map.of("get", Map.of(
                "tags", List.of("UC-2: Restaurant Search"),
                "summary", "Search restaurants with keyword, cuisine, and pure-veg filters",
                "responses", Map.of("200", Map.of("description", "List of matching restaurants"))
        )));
        paths.put("/api/v1/restaurants/{id}/menu", Map.of("get", Map.of(
                "tags", List.of("UC-3: View Menu"),
                "summary", "Retrieve categorized menu items for a restaurant",
                "responses", Map.of("200", Map.of("description", "List of menu items with prices in INR"))
        )));

        // UC-4: Place Order & Payments
        paths.put("/api/v1/orders", Map.of("post", Map.of(
                "tags", List.of("UC-4: Place Order"),
                "summary", "Place a new order with 5% GST calculation and item validation",
                "responses", Map.of("201", Map.of("description", "Order created and payment settled"))
        )));
        paths.put("/api/v1/orders/user/{userId}", Map.of("get", Map.of(
                "tags", List.of("UC-4: Place Order"),
                "summary", "List past and active orders for a customer"
        )));

        // UC-5: Track Order
        paths.put("/api/v1/tracking/{orderId}", Map.of("get", Map.of(
                "tags", List.of("UC-5: Track Order"),
                "summary", "Get real-time live delivery telemetry, driver details, and milestone progression"
        )));
        paths.put("/api/v1/tracking/{orderId}/status", Map.of("post", Map.of(
                "tags", List.of("UC-5: Track Order"),
                "summary", "Advance order delivery milestone (PLACED -> PREPARING -> OUT_FOR_DELIVERY -> DELIVERED)"
        )));

        // UC-6: Review and Rate Order
        paths.put("/api/v1/reviews", Map.of("post", Map.of(
                "tags", List.of("UC-6: Review and Rate Order"),
                "summary", "Submit 1-5 star review and recalculate restaurant aggregate rating"
        )));

        // UC-8: Save Favorite Restaurants
        paths.put("/api/v1/favorites/toggle", Map.of("post", Map.of(
                "tags", List.of("UC-8: Save Favorite Restaurants"),
                "summary", "Toggle 1-click favorite bookmark for a restaurant"
        )));
        paths.put("/api/v1/favorites/user/{userId}", Map.of("get", Map.of(
                "tags", List.of("UC-8: Save Favorite Restaurants"),
                "summary", "Retrieve all favorited restaurants for a customer"
        )));

        // UC-9: Update Delivery Address
        paths.put("/api/v1/addresses/user/{userId}", Map.of("get", Map.of(
                "tags", List.of("UC-9: Update Delivery Address"),
                "summary", "List all saved delivery addresses for a customer"
        )));
        paths.put("/api/v1/addresses", Map.of("post", Map.of(
                "tags", List.of("UC-9: Update Delivery Address"),
                "summary", "Add a new delivery address"
        )));

        // UC-10: Customer Support Request
        paths.put("/api/v1/support/tickets", Map.of("post", Map.of(
                "tags", List.of("UC-10: Customer Support Request"),
                "summary", "Create a customer care support ticket"
        )));
        paths.put("/api/v1/support/tickets/user/{userId}", Map.of("get", Map.of(
                "tags", List.of("UC-10: Customer Support Request"),
                "summary", "Retrieve support tickets and resolution statuses for a customer"
        )));

        spec.put("paths", paths);
        return ResponseEntity.ok(spec);
    }
}
