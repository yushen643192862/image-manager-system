package com.imageplatform.dto.Response;

import com.imageplatform.entity.Tag;
import lombok.Data;

import java.util.List;

@Data
public class TagGetResponse {
    List<Tag> tags;
}
