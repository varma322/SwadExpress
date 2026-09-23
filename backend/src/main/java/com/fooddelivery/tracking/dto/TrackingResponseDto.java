package com.fooddelivery.tracking.dto;

import com.fooddelivery.order.entity.OrderStatus;
import java.time.LocalDateTime;

public class TrackingResponseDto {

    private Long id;
    private Long orderId;
    private OrderStatus status;
    private Integer estimatedDeliveryMinutes;
    private String driverName;
    private String driverPhone;
    private String vehicleType;
    private Double driverRating;
    private Double driverLatitude;
    private Double driverLongitude;
    private Integer currentStep;
    private String statusMessage;
    private LocalDateTime lastUpdated;

    public TrackingResponseDto() {}

    public TrackingResponseDto(Long id, Long orderId, OrderStatus status, Integer estimatedDeliveryMinutes,
                               String driverName, String driverPhone, String vehicleType, Double driverRating,
                               Double driverLatitude, Double driverLongitude, Integer currentStep,
                               String statusMessage, LocalDateTime lastUpdated) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.estimatedDeliveryMinutes = estimatedDeliveryMinutes;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.vehicleType = vehicleType;
        this.driverRating = driverRating;
        this.driverLatitude = driverLatitude;
        this.driverLongitude = driverLongitude;
        this.currentStep = currentStep;
        this.statusMessage = statusMessage;
        this.lastUpdated = lastUpdated;
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
