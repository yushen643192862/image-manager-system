package com.imageplatform.dto.Request;

import lombok.Data;

@Data
public class ImageUploadRequest {
    private Integer userId;

    private String title;
    private String description;

    private Integer width = 200;
    private Integer height = 200;
}