package com.fooddelivery.support.dto;

import java.time.LocalDateTime;

public class TicketResponseDto {

    private Long id;
    private String ticketNumber;
    private Long userId;
    private Long orderId;
    private String category;
    private String description;
    private String status;
    private String resolutionNotes;
    private LocalDateTime createdAt;

    public TicketResponseDto() {}

    public TicketResponseDto(Long id, String ticketNumber, Long userId, Long orderId, String category,
                             String description, String status, String resolutionNotes, LocalDateTime createdAt) {
        this.id = id;
        this.ticketNumber = ticketNumber;
        this.userId = userId;
        this.orderId = orderId;
        this.category = category;
        this.description = description;
        this.status = status;
        this.resolutionNotes = resolutionNotes;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
