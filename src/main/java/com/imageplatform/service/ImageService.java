package com.imageplatform.service;

import com.imageplatform.dto.Request.UpdateImageInforRequest;
import com.imageplatform.dto.Response.GetOriginalImageResponse;
import com.imageplatform.dto.Response.GetThumbnailImageResponse;
import com.imageplatform.dto.Request.GetThumbnailImageRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;


import java.nio.file.Path;

public interface ImageService {
    /**
     * 首页获取瀑布流
     * @param request 图片请求类
     * @return 图片响应（包含URL）
     */

    GetThumbnailImageResponse GetImageList(GetThumbnailImageRequest request);
     /** 上传图片
     * @param file 图片文件
     * @param userId 用户ID，标识图片上传者
     * @return 无
     */
    Void uploadImage(MultipartFile file, Integer userId);
     /** 返回缩略图片路径
     * @param imageId 图片ID
     * @return 缩略图路径
     */
    Path findThumbnailPath(Integer imageId);
    /** 返回原图片
     * @param imageId 图片ID
     * @return 原路径
     */
    Path findOriginalPath(Integer imageId);

    GetOriginalImageResponse getOriginalDetail(Integer imageId);

    Void updateImageinfor(Integer imageId, UpdateImageInforRequest request);

    Void updateImage(Integer imageId, MultipartFile file);

    Void deleteImage(Integer imageId);
}