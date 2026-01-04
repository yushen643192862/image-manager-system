package com.imageplatform.dto.Request;

import lombok.Data;

import java.util.List;
@Data
public class UpdateImageInforRequest {
    String title;
    String description;
    List<Integer> tags;
}
