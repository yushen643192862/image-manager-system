package com.imageplatform.dto.Response;
import lombok.Data;

/**
 * 登录响应DTO
 * 用于返回给前端的登录结果
 */
@Data
public class LoginResponse {
    private String token;
    private String avatarUrl;
}