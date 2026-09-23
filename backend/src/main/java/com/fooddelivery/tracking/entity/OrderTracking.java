package com.fooddelivery.tracking.entity;

import com.fooddelivery.order.entity.OrderStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_tracking")
public class OrderTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PLACED;

    private Integer estimatedDeliveryMinutes = 25;

    private String driverName = "Alex Rivera";

    private String driverPhone = "+1 (555) 349-8201";

    private String vehicleType = "Honda Civic (Silver, #7X-882)";

    private Double driverRating = 4.9;

    private Double driverLatitude = 37.7749;

    private Double driverLongitude = -122.4194;

    private Integer currentStep = 1; // 1: Placed, 2: Preparing, 3: Out for delivery, 4: Delivered

    private String statusMessage = "Order has been confirmed by the restaurant.";

    private LocalDateTime lastUpdated = LocalDateTime.now();

    public OrderTracking() {}

    public OrderTracking(Long orderId, OrderStatus status, Integer estimatedDeliveryMinutes,
                         String driverName, String driverPhone, String vehicleType, Double driverRating) {
        this.orderId = orderId;
        this.status = status;
        this.estimatedDeliveryMinutes = estimatedDeliveryMinutes;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.vehicleType = vehicleType;
        this.driverRating = driverRating;
        this.currentStep = 1;
        this.statusMessage = "Order received and confirmed by kitchen.";
        this.lastUpdated = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public Integer getEstimatedDeliveryMinutes() { return estimatedDeliveryMinutes; }
    public void setEstimatedDeliveryMinutes(Integer estimatedDeliveryMinutes) { this.estimatedDeliveryMinutes = estimatedDeliveryMinutes; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public Double getDriverRating() { return driverRating; }
    public void setDriverRating(Double driverRating) { this.driverRating = driverRating; }

    public Double getDriverLatitude() { return driverLatitude; }
    public void setDriverLatitude(Double driverLatitude) { this.driverLatitude = driverLatitude; }

    public Double getDriverLongitude() { return driverLongitude; }
    public void setDriverLongitude(Double driverLongitude) { this.driverLongitude = driverLongitude; }

    public Integer getCurrentStep() { return currentStep; }
    public void setCurrentStep(Integer currentStep) { this.currentStep = currentStep; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
