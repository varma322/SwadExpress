package com.fooddelivery.tracking.controller;

import com.fooddelivery.tracking.dto.TrackingResponseDto;
import com.fooddelivery.tracking.service.TrackingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tracking")
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<TrackingResponseDto> getTracking(@PathVariable Long orderId) {
        TrackingResponseDto tracking = trackingService.getTracking(orderId);
        return ResponseEntity.ok(tracking);
    }

    @PostMapping("/{orderId}/advance-step")
    public ResponseEntity<TrackingResponseDto> advanceStep(@PathVariable Long orderId) {
        TrackingResponseDto updated = trackingService.advanceStep(orderId);
        return ResponseEntity.ok(updated);
    }
}
