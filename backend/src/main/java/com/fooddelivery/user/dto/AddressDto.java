package com.fooddelivery.user.dto;

import jakarta.validation.constraints.NotBlank;

public class AddressDto {

    private Long id;
    private Long userId;

    @NotBlank(message = "Address label is required (e.g. Home, Work)")
    private String label;

    @NotBlank(message = "Street address is required")
    private String street;

    private String suite;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Zip code is required")
    private String zipCode;

    private boolean isDefault;

    public AddressDto() {}

    public AddressDto(Long id, Long userId, String label, String street, String suite, String city, String state, String zipCode, boolean isDefault) {
        this.id = id;
        this.userId = userId;
        this.label = label;
        this.street = street;
        this.suite = suite;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.isDefault = isDefault;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getSuite() { return suite; }
    public void setSuite(String suite) { this.suite = suite; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
}
