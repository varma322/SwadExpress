package com.fooddelivery.support.controller;

import com.fooddelivery.support.dto.CreateTicketDto;
import com.fooddelivery.support.dto.TicketResponseDto;
import com.fooddelivery.support.service.SupportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/support")
public class SupportController {

    private final SupportService supportService;

    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    @PostMapping("/tickets")
    public ResponseEntity<TicketResponseDto> createTicket(@Valid @RequestBody CreateTicketDto dto) {
        TicketResponseDto ticket = supportService.createTicket(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }

    @GetMapping("/tickets/user/{userId}")
    public ResponseEntity<List<TicketResponseDto>> getUserTickets(@PathVariable Long userId) {
        List<TicketResponseDto> tickets = supportService.getUserTickets(userId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<TicketResponseDto> getTicketById(@PathVariable Long id) {
        TicketResponseDto ticket = supportService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }
}
