package com.imageplatform.entity;

import lombok.Data;
import java.time.LocalDateTime;
@Data
public class Token {
    private Integer id;
    private Integer userId;
    private String token;
    private String deviceInfo;
    private String ipAddress;
    private LocalDateTime expiryTime;
    private boolean isValid;

    public boolean isExpired() {
        return expiryTime != null && expiryTime.isBefore(LocalDateTime.now());
    }

    public boolean isValidToken() {
        return isValid && !isExpired();
    }
}