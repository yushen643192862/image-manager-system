package com.imageplatform.dto.Response;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ReturnImg {
    private Integer id;
    private Integer fileSize;
    private LocalDateTime uploadTime;
    private String title;
    private String description;
    private Integer width;
    private Integer height;
    private String cameraModel;
    private LocalDateTime takenTime;
    private Float latitude;
    private Float longitude;
}
