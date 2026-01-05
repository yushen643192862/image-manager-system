package com.imageplatform.controller;

import com.imageplatform.dto.Request.LoginRequest;
import com.imageplatform.dto.Request.UpdateUserRequest;
import com.imageplatform.dto.Response.ApiResponse;
import com.imageplatform.dto.Response.LoginResponse;
import com.imageplatform.mapper.UserMapper;
import com.imageplatform.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController  // 表明这是REST控制器
@RequestMapping("/api/user")  // 基础路径
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    @Autowired  // 自动注入Service
    private UserService userservice;

    /**
     * 用户信息更新
     * POST http://localhost:8081/api/user/update
     */
    @PutMapping("/update")
    public ApiResponse<String> updateUserInfo(
            @RequestBody UpdateUserRequest request,
            HttpServletRequest httpRequest) {

        // 从请求头获取 Authorization
        String authHeader = httpRequest.getHeader("Authorization");
        System.out.println("🔍 Authorization 头: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Token 格式错误或不存在");
            return ApiResponse.error(403, "未授权");
        }

        try {
            String token = authHeader.substring(7); // 去掉 "Bearer "
            System.out.println("提取的Token: " + token.substring(0, Math.min(token.length(), 20)) + "...");

            userservice.updateUserInfo(token, request);

            return ApiResponse.success("用户信息更新成功", null);

        } catch (Exception e) {
            System.out.println(" 更新失败: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error(500, "更新失败: " + e.getMessage());
        }
    }
    /**
     * 获取头像
     * POST http://localhost:8081/api/user/avatars
     */
    @GetMapping("/image/{imageId}/thumbnail")
    public ResponseEntity<Resource> getThumbnail(@PathVariable Integer userId) {
        try {

            Path thumbnailPath = userservice.findAvatarPath(userId);

            if (!Files.exists(thumbnailPath)) {
                return ResponseEntity.notFound().build();
            }

            // 2. 创建Resource对象
            Resource resource = new UrlResource(thumbnailPath.toUri());

            // 3. 确定文件类型
            String contentType = determineContentType(thumbnailPath);

            // 4. 返回文件（直接返回ResponseEntity<Resource>，不要ApiResponse包装）
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"thumbnail_" + userId + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
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
            // 尝试探测类型
            try {
                String detectedType = Files.probeContentType(filePath);
                return detectedType != null ? detectedType : "application/octet-stream";
            } catch (IOException e) {
                return "application/octet-stream";
            }
        }
    }
}

