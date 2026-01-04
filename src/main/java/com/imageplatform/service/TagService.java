package com.imageplatform.service;


import com.imageplatform.dto.Request.TagCreateRequest;
import com.imageplatform.dto.Request.TagDeleteRequest;
import com.imageplatform.dto.Request.TagGetRequest;
import com.imageplatform.dto.Response.TagGetResponse;

import java.time.LocalDateTime;


public interface TagService {
    /**
     * 获取标签列表
     * @param userId 用户ID
     * @return 标签响应
     */
    TagGetResponse getTag(Integer userId);
    /**
     * 删除标签
     * @param request 标签请求类
     * @return null
     */
    Void deleteTag(TagDeleteRequest request);
    /**
     * 创建标签
     * @param request 标签创建请求类
     * @return null
     */
    LocalDateTime createTag(TagCreateRequest request);
}
