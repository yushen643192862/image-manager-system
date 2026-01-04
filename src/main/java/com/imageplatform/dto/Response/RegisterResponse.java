package com.imageplatform.dto.Response;

import lombok.Data;

/**
 * 注册响应DTO
 * 用于返回给前端的注册结果
 */
@Data
public class RegisterResponse {
    private String Username;
    private String email;
    private String avatarUrl;
}
