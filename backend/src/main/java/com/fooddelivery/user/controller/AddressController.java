package com.fooddelivery.user.controller;

import com.fooddelivery.user.dto.AddressDto;
import com.fooddelivery.user.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ResponseEntity<List<AddressDto>> getAddresses(@PathVariable Long userId) {
        List<AddressDto> addresses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(addresses);
    }

    @PostMapping
    public ResponseEntity<AddressDto> addAddress(@PathVariable Long userId, @Valid @RequestBody AddressDto dto) {
        AddressDto created = addressService.addAddress(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{addressId}/default")
    public ResponseEntity<AddressDto> setDefaultAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        AddressDto updated = addressService.setDefaultAddress(userId, addressId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }
}
