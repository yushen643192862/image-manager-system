package com.imageplatform.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResetToken {
    private Integer id;
    private Integer userId;
    private String token;
    private LocalDateTime expiryTime;
    private boolean used;
}
