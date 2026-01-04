package com.imageplatform.dto.Response;
import lombok.Data;
import com.imageplatform.dto.Response.ReturnImg;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetThumbnailImageResponse {
    Boolean isend;
    List<ReturnImg> returnImg;
    public GetThumbnailImageResponse() {
        this.returnImg = new ArrayList<>();
    }
}
