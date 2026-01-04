package com.imageplatform.dto.Request;

import lombok.Data;

@Data
public class GetThumbnailImageRequest {
    Integer userid;
    Integer pagenum;
    Integer sizenum;
}
