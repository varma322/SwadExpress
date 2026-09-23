package com.fooddelivery.tracking.service;

import com.fooddelivery.common.exception.ResourceNotFoundException;
import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.repository.OrderRepository;
import com.fooddelivery.tracking.dto.TrackingResponseDto;
import com.fooddelivery.tracking.entity.OrderTracking;
import com.fooddelivery.tracking.repository.OrderTrackingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class TrackingService {

    private final OrderTrackingRepository trackingRepository;
    private final OrderRepository orderRepository;

    public TrackingService(OrderTrackingRepository trackingRepository, OrderRepository orderRepository) {
        this.trackingRepository = trackingRepository;
        this.orderRepository = orderRepository;
    }

    public OrderTracking initializeTracking(Long orderId) {
        OrderTracking tracking = new OrderTracking(
                orderId,
                OrderStatus.PLACED,
                25,
                "Alex Rivera",
                "+1 (555) 349-8201",
                "Honda Civic (Silver, #7X-882)",
                4.9
        );
        tracking.setCurrentStep(1);
        tracking.setStatusMessage("Order received by restaurant. Kitchen is preparing to cook.");
        return trackingRepository.save(tracking);
    }

    @Transactional(readOnly = true)
    public TrackingResponseDto getTracking(Long orderId) {
        OrderTracking tracking = trackingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Tracking telemetry not found for order: " + orderId));
        return toDto(tracking);
    }

    public TrackingResponseDto advanceStep(Long orderId) {
        OrderTracking tracking = trackingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Tracking telemetry not found for order: " + orderId));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        int step = tracking.getCurrentStep();
        if (step == 1) {
            tracking.setCurrentStep(2);
            tracking.setStatus(OrderStatus.PREPARING);
            tracking.setEstimatedDeliveryMinutes(18);
            tracking.setStatusMessage("Chef is cooking your fresh meal in the kitchen.");
            order.setStatus(OrderStatus.PREPARING);
        } else if (step == 2) {
            tracking.setCurrentStep(3);
            tracking.setStatus(OrderStatus.OUT_FOR_DELIVERY);
            tracking.setEstimatedDeliveryMinutes(10);
            tracking.setStatusMessage("Courier picked up the meal and is en route to your address!");
            tracking.setDriverLatitude(37.7780);
            tracking.setDriverLongitude(-122.4150);
            order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        } else if (step == 3) {
            tracking.setCurrentStep(4);
            tracking.setStatus(OrderStatus.DELIVERED);
            tracking.setEstimatedDeliveryMinutes(0);
            tracking.setStatusMessage("Delivered! Enjoy your delicious meal.");
            tracking.setDriverLatitude(37.7833);
            tracking.setDriverLongitude(-122.4167);
            order.setStatus(OrderStatus.DELIVERED);
        }

        tracking.setLastUpdated(LocalDateTime.now());
        orderRepository.save(order);
        OrderTracking updated = trackingRepository.save(tracking);
        return toDto(updated);
    }

    private TrackingResponseDto toDto(OrderTracking t) {
        return new TrackingResponseDto(
                t.getId(),
                t.getOrderId(),
                t.getStatus(),
                t.getEstimatedDeliveryMinutes(),
                t.getDriverName(),
                t.getDriverPhone(),
                t.getVehicleType(),
                t.getDriverRating(),
                t.getDriverLatitude(),
                t.getDriverLongitude(),
                t.getCurrentStep(),
                t.getStatusMessage(),
                t.getLastUpdated()
        );
    }
}
