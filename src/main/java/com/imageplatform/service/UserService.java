package com.imageplatform.service;

import com.imageplatform.dto.Request.UpdateUserRequest;

import java.nio.file.Path;

public interface UserService {
    /**
     * 更新用户信息
     * @param token JWT令牌
     * @param request 更新请求DTO
     */
    Void updateUserInfo(String token, UpdateUserRequest request);
    /** 返回头像
     * @param imageId 图片ID
     * @return 原路径
     */
    Path findAvatarPath(Integer imageId);
}
