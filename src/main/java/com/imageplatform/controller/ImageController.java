package com.imageplatform.controller;
import com.imageplatform.dto.Request.UpdateImageInforRequest;
import com.imageplatform.dto.Response.ApiResponse;
import com.imageplatform.dto.Response.GetOriginalImageResponse;
import com.imageplatform.dto.Response.GetThumbnailImageResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import org.apache.commons.imaging.ImageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.imageplatform.dto.Request.GetThumbnailImageRequest;
import com.imageplatform.service.ImageService;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController  // 表明这是REST控制器
@RequestMapping("/api/auth")  // 基础路径
@CrossOrigin(origins = "http://localhost:5173")
public class ImageController {

    @Autowired  // 自动注入Service
    private ImageService imageService;

    /**
     * 首页图片瀑布流接口
     * POST http://localhost:8081/api/auth/image
     */
    @GetMapping("/image")
    public ApiResponse<GetThumbnailImageResponse> getHomePageImage(
            @RequestParam @Min(1) Integer userid,
            @RequestParam(defaultValue = "1") @Min(1) Integer pagenum,
            @RequestParam(defaultValue = "20") @Min(1) Integer sizenum,
            HttpServletRequest httpRequest) {

        try {
            GetThumbnailImageRequest request = new GetThumbnailImageRequest();
            request.setUserid(userid);
            request.setPagenum(pagenum);
            request.setSizenum(sizenum);

            GetThumbnailImageResponse response = imageService.GetImageList(request);
            return ApiResponse.success("图片获取成功", response);
        } catch (RuntimeException e) {
            return ApiResponse.error(2001, e.getMessage());
        }
    }
    /**
     * 图片上传接口
     * POST http://localhost:8081/api/auth/upload
     */
    @PostMapping("/upload")
    public ApiResponse<Void> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Integer userId) {  // 这个参数是必需的！ {
        try {
            if(file.isEmpty()) {
                return ApiResponse.error(2003, "上传失败：上传为空");
            }
            imageService.uploadImage(
                    file,
                    userId
            );
            return ApiResponse.success("图片上传成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(2001, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(2002, "上传失败：" + e.getMessage());
        }
    }
    /**
     * 获取缩略图
     * GET http://localhost:8081/api/auth/{imageId}/thumbnail
     */
    @GetMapping("/image/{imageId}/thumbnail")
    public ResponseEntity<Resource> getThumbnail(@PathVariable Integer imageId) {
        try {
            Path thumbnailPath = imageService.findThumbnailPath(imageId);

            if (!Files.exists(thumbnailPath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(thumbnailPath.toUri());
            String contentType = determineContentType(thumbnailPath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"thumbnail_" + imageId + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据文件扩展名确定Content-Type
     */
    private String determineContentType(Path filePath) {
        String fileName = filePath.getFileName().toString().toLowerCase();

        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.endsWith(".bmp")) {
            return "image/bmp";
        } else if (fileName.endsWith(".webp")) {
            return "image/webp";
        } else {
            try {
                String detectedType = Files.probeContentType(filePath);
                return detectedType != null ? detectedType : "application/octet-stream";
            } catch (IOException e) {
                return "application/octet-stream";
            }
        }
    }

    /**
     * 获取原图
     */
    @GetMapping("/image/{imageId}/original")
    public ResponseEntity<Resource> getOriginalImage(@PathVariable Integer imageId) {
        try {
            Path originalPath = imageService.findOriginalPath(imageId);

            if (!Files.exists(originalPath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(originalPath.toUri());
            String contentType = determineContentType(originalPath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"original_" + imageId + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/image/{imageId}/detail")
    public ApiResponse<GetOriginalImageResponse> getOriginalImageDetail(@PathVariable Integer imageId) {
        try{
            GetOriginalImageResponse response = imageService.getOriginalDetail(imageId);
            return ApiResponse.success("图片信息获取成功", response);
        } catch (RuntimeException e) {
            return ApiResponse.error(2001, e.getMessage());
        }
    }
    @PutMapping("/image/{imageId}/update")
    public ApiResponse<Void> updateImageinfor(@PathVariable Integer imageId, @RequestBody UpdateImageInforRequest request) {
        try{
            imageService.updateImageinfor(imageId, request);
            return ApiResponse.success("图片信息更新成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(2001, e.getMessage());
        }
    }
    @PutMapping("/image/{imageId}/updateimage")
    public ApiResponse<Void> updateImage(
            @RequestParam("file") MultipartFile file,
            @PathVariable("imageId") Integer imageId) {
        try{
            imageService.updateImage(imageId, file);
            return ApiResponse.success("图片更新成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(2001, e.getMessage());
        }
    }
    @DeleteMapping("/image/{imageId}/delete")
    public ApiResponse<Void> deleteImage(
            @PathVariable("imageId") Integer imageId) {
        try{
            imageService.deleteImage(imageId);
            return ApiResponse.success("图片删除成功", null);
        } catch (RuntimeException e) {
            return ApiResponse.error(2001, e.getMessage());
        }
    }
}
