package com.imageplatform.service.impl;

import com.imageplatform.dto.Request.UpdateUserRequest;
import com.imageplatform.entity.User;
import com.imageplatform.mapper.UserMapper;
import com.imageplatform.service.UserService;
import com.imageplatform.common.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtTokenUtil jwtUtil;

    public UserServiceImpl(UserMapper userMapper, JwtTokenUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    // 头像配置
    @Value("${app.avatar.upload-dir:uploads/avatars/}")
    private String avatarUploadDir;

    @Value("${app.avatar.max-size:5242880}") // 5MB
    private long maxAvatarSize;

    @Value("${app.avatar.thumbnail-width:100}")
    private int thumbnailWidth;

    @Override
    @Transactional
    public Void updateUserInfo(String token, UpdateUserRequest request) {
        // 从token中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(token);
        log.info("更新用户信息，用户ID: {}", userId);

        // 查询当前用户
        User currentUser = userMapper.findById(userId.intValue());
        if (currentUser == null) {
            throw new RuntimeException("用户不存在");
        }

        boolean hasChanges = false;

        // 更新用户名
        if (StringUtils.hasText(request.getUsername())) {
            String newUsername = request.getUsername().trim();
            if (!newUsername.equals(currentUser.getUsername())) {
                // 检查用户名是否已被其他用户使用
                User existingUser = userMapper.findByUsername(newUsername);
                if (existingUser != null && !existingUser.getId().equals(userId)) {
                    throw new RuntimeException("用户名已被使用");
                }
                currentUser.setUsername(newUsername);
                hasChanges = true;
                log.info("用户名更新: {} -> {}", currentUser.getUsername(), newUsername);
            }
        }

        // 处理头像
        if (request.getAvatar() != null) {
            if (StringUtils.hasText(request.getAvatar())) {
                // 有新头像上传
                try {
                    String newAvatarPath = saveAvatarAndThumbnail(request.getAvatar(), userId);

                    // 删除旧头像文件
                    deleteOldAvatar(currentUser.getAvatarUrl());

                    currentUser.setAvatarUrl(newAvatarPath);
                    hasChanges = true;
                    log.info("头像更新成功: {}", newAvatarPath);

                } catch (Exception e) {
                    log.error("保存头像失败", e);
                    throw new RuntimeException("头像保存失败: " + e.getMessage());
                }
            } else {
                // 清空头像
                deleteOldAvatar(currentUser.getAvatarUrl());
                currentUser.setAvatarUrl(null);
                hasChanges = true;
                log.info("头像已清空");
            }
        }
        // 如果avatar为null，表示前端没传这个字段，保持原头像不变

        // 如果没有变化，直接返回
        if (!hasChanges) {
            log.info("用户信息无变化，无需更新");
            return null;
        }

        // 保存到数据库
        int result = userMapper.update(currentUser);
        if (result <= 0) {
            throw new RuntimeException("更新用户信息失败");
        }

        log.info("用户信息更新成功，用户ID: {}", userId);
        return null;
    }

    /**
     * 保存头像并生成缩略图
     * @param base64Image Base64编码的图片
     * @param userId 用户ID
     * @return 缩略图路径
     */
    private String saveAvatarAndThumbnail(String base64Image, Long userId) throws IOException {
        try {
            // === 1. 解码Base64 ===
            String imageString;
            if (base64Image.contains(",")) {
                imageString = base64Image.split(",")[1];
            } else {
                imageString = base64Image;
            }

            byte[] imageBytes = Base64.getDecoder().decode(imageString);

            // 验证图片大小
            if (imageBytes.length > maxAvatarSize) {
                throw new RuntimeException(String.format(
                        "图片大小不能超过 %.1fMB", maxAvatarSize / 1024.0 / 1024.0));
            }

            // === 2. 创建上传目录 ===
            Path uploadDir = Paths.get(avatarUploadDir);
            Files.createDirectories(uploadDir);

            // === 3. 生成唯一文件名 ===
            String uuid = UUID.randomUUID().toString();
            String thumbnailFileName = "thumb_" + uuid + ".jpg";
            Path thumbnailPath = uploadDir.resolve(thumbnailFileName);

            // === 4. 使用Thumbnailator生成缩略图 ===
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
                // 读取图片信息
                BufferedImage originalImage = ImageIO.read(inputStream);
                if (originalImage == null) {
                    throw new RuntimeException("无法读取图片文件");
                }

                // 获取图片尺寸
                int imgWidth = originalImage.getWidth();
                int imgHeight = originalImage.getHeight();
                log.info("头像尺寸: {}x{}", imgWidth, imgHeight);

                // 重置流位置
                inputStream.reset();

                // 生成缩略图
                Thumbnails.of(inputStream)
                        .width(thumbnailWidth)
                        .keepAspectRatio(true)
                        .outputQuality(0.9)
                        .outputFormat("jpg")
                        .toFile(thumbnailPath.toFile());
            }

            log.info("头像缩略图生成成功: {}", thumbnailPath);

            // 返回相对路径
            return "/uploads/avatars/" + thumbnailFileName;

        } catch (Exception e) {
            throw new IOException("处理头像失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除旧头像文件
     */
    private void deleteOldAvatar(String oldAvatarPath) {
        if (StringUtils.hasText(oldAvatarPath)) {
            try {
                // 从URL中提取文件名
                String fileName = oldAvatarPath.substring(oldAvatarPath.lastIndexOf("/") + 1);
                Path oldPath = Paths.get(avatarUploadDir, fileName);

                if (Files.exists(oldPath)) {
                    Files.delete(oldPath);
                    log.info("删除旧头像文件: {}", oldPath);
                }
            } catch (Exception e) {
                log.warn("删除旧头像文件失败: {}", e.getMessage());
            }
        }
    }

    @Override
    public Path findAvatarPath(Integer userId){
        User user = userMapper.findById(userId);
        return Paths.get(user.getAvatarUrl());
    }
}