package com.imageplatform.dto.Request;

import lombok.Data;

@Data
public class ForgetRequest {
    private String email;
    private String newPassword;
}
