package com.imageplatform.dto.Response;

import com.imageplatform.entity.Tag;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class GetOriginalImageResponse {
    Integer imageId;
    String title;
    String description;
    Integer size;
    LocalDateTime uploadTime;
    Integer width;
    Integer height;
    String cameraModel;
    LocalDateTime takenTime;
    Float latitude;
    Float longitude;
    List<Tag> tags;
}
