package com.fooddelivery.support.service;

import com.fooddelivery.common.exception.ResourceNotFoundException;
import com.fooddelivery.support.dto.CreateTicketDto;
import com.fooddelivery.support.dto.TicketResponseDto;
import com.fooddelivery.support.entity.SupportTicket;
import com.fooddelivery.support.repository.SupportTicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupportService {

    private final SupportTicketRepository supportTicketRepository;
    private final Random random = new Random();

    public SupportService(SupportTicketRepository supportTicketRepository) {
        this.supportTicketRepository = supportTicketRepository;
    }

    public TicketResponseDto createTicket(CreateTicketDto dto) {
        String ticketNumber = "TCK-" + (10000 + random.nextInt(90000));

        SupportTicket ticket = new SupportTicket(
                ticketNumber,
                dto.getUserId(),
                dto.getOrderId(),
                dto.getCategory().toUpperCase(),
                dto.getDescription().trim()
        );
        ticket.setResolutionNotes("Ticket assigned to Customer Experience Specialist. Typical response under 15 minutes.");

        SupportTicket saved = supportTicketRepository.save(ticket);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<TicketResponseDto> getUserTickets(Long userId) {
        return supportTicketRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TicketResponseDto getTicketById(Long id) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Support ticket not found with id: " + id));
        return toDto(ticket);
    }

    private TicketResponseDto toDto(SupportTicket t) {
        return new TicketResponseDto(
                t.getId(),
                t.getTicketNumber(),
                t.getUserId(),
                t.getOrderId(),
                t.getCategory(),
                t.getDescription(),
                t.getStatus(),
                t.getResolutionNotes(),
                t.getCreatedAt()
        );
    }
}
