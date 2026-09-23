package com.fooddelivery.user.service;

import com.fooddelivery.common.exception.ResourceNotFoundException;
import com.fooddelivery.user.dto.AddressDto;
import com.fooddelivery.user.entity.Address;
import com.fooddelivery.user.repository.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Transactional(readOnly = true)
    public List<AddressDto> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserId(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AddressDto addAddress(Long userId, AddressDto dto) {
        List<Address> existing = addressRepository.findByUserId(userId);
        boolean isFirst = existing.isEmpty();

        if (dto.isDefault()) {
            existing.forEach(a -> a.setDefault(false));
            addressRepository.saveAll(existing);
        }

        Address address = new Address(
                userId,
                dto.getLabel().trim(),
                dto.getStreet().trim(),
                dto.getSuite() != null ? dto.getSuite().trim() : "",
                dto.getCity().trim(),
                dto.getState().trim(),
                dto.getZipCode().trim(),
                dto.isDefault() || isFirst
        );

        Address saved = addressRepository.save(address);
        return toDto(saved);
    }

    public AddressDto setDefaultAddress(Long userId, Long addressId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        Address target = null;
        for (Address a : addresses) {
            if (a.getId().equals(addressId)) {
                a.setDefault(true);
                target = a;
            } else {
                a.setDefault(false);
            }
        }
        if (target == null) {
            throw new ResourceNotFoundException("Address not found with id: " + addressId);
        }
        addressRepository.saveAll(addresses);
        return toDto(target);
    }

    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
        if (!address.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Address does not belong to user id: " + userId);
        }
        addressRepository.delete(address);
    }

    private AddressDto toDto(Address a) {
        return new AddressDto(
                a.getId(),
                a.getUserId(),
                a.getLabel(),
                a.getStreet(),
                a.getSuite(),
                a.getCity(),
                a.getState(),
                a.getZipCode(),
                a.isDefault()
        );
    }
}
