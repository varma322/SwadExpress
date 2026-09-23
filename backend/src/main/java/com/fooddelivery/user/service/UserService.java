package com.fooddelivery.user.service;

import com.fooddelivery.common.exception.BadRequestException;
import com.fooddelivery.common.exception.ResourceNotFoundException;
import com.fooddelivery.user.dto.UserLoginDto;
import com.fooddelivery.user.dto.UserProfileDto;
import com.fooddelivery.user.dto.UserRegistrationDto;
import com.fooddelivery.user.entity.User;
import com.fooddelivery.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileDto register(UserRegistrationDto dto) {
        String cleanEmail = dto.getEmail().trim().toLowerCase();
        String cleanPhone = dto.getPhone().trim();

        if (userRepository.existsByEmail(cleanEmail)) {
            throw new BadRequestException("An account with email " + cleanEmail + " already exists.");
        }
        if (userRepository.existsByPhone(cleanPhone)) {
            throw new BadRequestException("An account with phone number " + cleanPhone + " already exists.");
        }

        String defaultAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80";
        User user = new User(
                dto.getName().trim(),
                cleanEmail,
                cleanPhone,
                dto.getPassword(),
                defaultAvatar
        );

        User saved = userRepository.save(user);
        UserProfileDto response = toDto(saved);
        response.setConfirmationMessage("Welcome to SwadExpress, " + saved.getName() + "! A confirmation SMS has been dispatched to " + cleanPhone + " and a welcome confirmation email sent to " + cleanEmail + ".");
        response.setSmsStatus("DISPATCHED");
        response.setEmailStatus("DISPATCHED");
        response.setToken("mock-jwt-token-" + saved.getId() + "-" + System.currentTimeMillis());
        return response;
    }

    @Transactional(readOnly = true)
    public UserProfileDto login(UserLoginDto dto) {
        User user = userRepository.findByEmail(dto.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Invalid email or password."));

        if (!user.getPassword().equals(dto.getPassword())) {
            throw new BadRequestException("Invalid email or password.");
        }

        return toDto(user);
    }

    @Transactional(readOnly = true)
    public UserProfileDto getProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return toDto(user);
    }

    public UserProfileDto updateProfile(Long id, UserProfileDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName().trim());
        }
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            user.setPhone(dto.getPhone().trim());
        }
        if (dto.getAvatarUrl() != null && !dto.getAvatarUrl().isBlank()) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }

        User updated = userRepository.save(user);
        return toDto(updated);
    }

    private UserProfileDto toDto(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.getCreatedAt()
        );
    }
}
