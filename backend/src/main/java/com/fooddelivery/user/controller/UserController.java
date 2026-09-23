package com.fooddelivery.user.controller;

import com.fooddelivery.user.dto.UserLoginDto;
import com.fooddelivery.user.dto.UserProfileDto;
import com.fooddelivery.user.dto.UserRegistrationDto;
import com.fooddelivery.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserProfileDto> register(@Valid @RequestBody UserRegistrationDto dto) {
        UserProfileDto created = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login")
    public ResponseEntity<UserProfileDto> login(@Valid @RequestBody UserLoginDto dto) {
        UserProfileDto user = userService.login(dto);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getProfile(@PathVariable Long id) {
        UserProfileDto user = userService.getProfile(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfileDto> updateProfile(@PathVariable Long id, @RequestBody UserProfileDto dto) {
        UserProfileDto updated = userService.updateProfile(id, dto);
        return ResponseEntity.ok(updated);
    }
}
