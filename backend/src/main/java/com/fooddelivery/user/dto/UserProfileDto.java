package com.fooddelivery.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class UserProfileDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private String confirmationMessage;
    private String smsStatus;
    private String emailStatus;
    private String token;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public UserProfileDto() {}

    public UserProfileDto(Long id, String name, String email, String phone, String avatarUrl, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getConfirmationMessage() { return confirmationMessage; }
    public void setConfirmationMessage(String confirmationMessage) { this.confirmationMessage = confirmationMessage; }

    public String getSmsStatus() { return smsStatus; }
    public void setSmsStatus(String smsStatus) { this.smsStatus = smsStatus; }

    public String getEmailStatus() { return emailStatus; }
    public void setEmailStatus(String emailStatus) { this.emailStatus = emailStatus; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

