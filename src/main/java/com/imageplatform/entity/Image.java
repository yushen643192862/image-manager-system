package com.imageplatform.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Image {
    private Integer id;
    private Integer userId;
    private String originalFilename;
    private String storagePath;
    private String thumbnailPath;
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