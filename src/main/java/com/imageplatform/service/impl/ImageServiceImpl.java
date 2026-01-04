package com.imageplatform.service.impl;

import com.imageplatform.dto.Request.GetThumbnailImageRequest;
import com.imageplatform.dto.Response.GetThumbnailImageResponse;
import com.imageplatform.dto.Response.ReturnImg;
import com.imageplatform.entity.Image;
import com.imageplatform.mapper.ImageMapper;
import com.imageplatform.service.ImageService;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageMapper imageMapper;

    public ImageServiceImpl(ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    @Override
    public GetThumbnailImageResponse GetImageList(GetThumbnailImageRequest request) {
        Integer userID = request.getUserid();
        List<Image> images = imageMapper.getImageByUserId(userID);
        int begin = (request.getPagenum()-1)* request.getSizenum();
        int end = request.getPagenum()* request.getSizenum();
        boolean isend = end > images.size();
        end = isend ? images.size() : end;
        images = images.subList(begin, end);
        GetThumbnailImageResponse response = new GetThumbnailImageResponse();
        response.setIsend(isend);
        for (Image image : images) {
            ReturnImg returnImg = new ReturnImg();
            returnImg.setId(image.getId());
            returnImg.setFileSize(image.getFileSize());
            returnImg.setUploadTime(image.getUploadTime());
            returnImg.setTitle(image.getTitle());
            returnImg.setDescription(image.getDescription());
            returnImg.setWidth(image.getWidth());
            returnImg.setHeight(image.getHeight());
            returnImg.setCameraModel(image.getCameraModel());
            returnImg.setTakenTime(image.getTakenTime());
            returnImg.setLatitude(image.getLatitude());
            returnImg.setLongitude(image.getLongitude());
            response.getReturnImg().add(returnImg);
        }
        return response;
    }

    @Override
    public Void uploadImage(MultipartFile file, Integer userId) {
        try {
            // === 1. 保存原始图片到本地 ===
            String originalFilename = file.getOriginalFilename();

            // 原图保存路径
            String originalDir = "C:\\Users\\Lenovo\\Desktop\\BS\\final\\image-manager-system\\image\\";
            Files.createDirectories(Paths.get(originalDir));

            // 生成唯一文件名
            String uuid = UUID.randomUUID().toString();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String originalFileName = uuid + fileExtension;
            Path originalPath = Paths.get(originalDir + originalFileName);

            // 保存原图
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, originalPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // 获取图片尺寸
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            int imgWidth = originalImage.getWidth();
            int imgHeight = originalImage.getHeight();

            // === 2. 使用 Thumbnailator 生成缩略图并保存到本地 ===
            String thumbnailDir = "C:\\Users\\Lenovo\\Desktop\\BS\\final\\image-manager-system\\thumbnail\\";
            Files.createDirectories(Paths.get(thumbnailDir));

            String thumbnailFileName = "thumb_" + uuid + ".jpg";
            Path thumbnailPath = Paths.get(thumbnailDir + thumbnailFileName);


            // 使用 Thumbnailator 生成缩略图（保持宽高比，质量高）
            Thumbnails.of(originalPath.toFile())
                    .width(300)
                    .keepAspectRatio(true)          // 保持宽高比
                    .outputQuality(0.9)             // 输出质量
                    .outputFormat("jpg")            // 输出格式
                    .toFile(thumbnailPath.toFile());

            // === 3. 使用 commons-imaging 提取 EXIF 信息 ===
            String cameraModel = null;
            LocalDateTime takenTime = null;
            Float latitude = null;
            Float longitude = null;

            try {
                // 读取图片元数据
                ImageMetadata metadata = Imaging.getMetadata(file.getBytes());

                if (metadata != null && metadata instanceof JpegImageMetadata) {
                    JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

                    // 获取EXIF信息
                    TiffImageMetadata exif = jpegMetadata.getExif();
                    if (exif != null) {
                        // 提取相机型号 - 使用正确的方式
                        Object modelValue = exif.getFieldValue(
                                org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants.TIFF_TAG_MODEL);
                        if (modelValue != null) {
                            cameraModel = modelValue.toString();
                        }

                        // 提取拍摄时间 - 使用正确的方式
                        Object dateValue = exif.getFieldValue(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
                        if (dateValue != null) {
                            String dateStr = dateValue.toString();
                            // EXIF日期格式: "2023:01:15 14:30:25"
                            try {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
                                takenTime = LocalDateTime.parse(dateStr, formatter);
                            } catch (Exception e) {
                                System.out.println("日期解析失败: " + dateStr);
                            }
                        }

                        // 提取GPS信息
                        TiffImageMetadata.GPSInfo gpsInfo = exif.getGPS();
                        if (gpsInfo != null) {
                            latitude = (float) gpsInfo.getLatitudeAsDegreesNorth();
                            longitude = (float) gpsInfo.getLongitudeAsDegreesEast();
                        }
                    }
                }
            } catch (ImageReadException e) {
                System.out.println("无法读取EXIF信息: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("读取文件失败: " + e.getMessage());
            }

            // === 4. 创建Image对象并设置所有信息 ===
            Image image = new Image();

            // 基本信息
            image.setUserId(userId);
            image.setOriginalFilename(originalFilename);
            image.setStoragePath(originalPath.toString());
            image.setThumbnailPath(thumbnailPath.toString());
            image.setFileSize((int) file.getSize());
            image.setUploadTime(LocalDateTime.now());
            image.setWidth(imgWidth);
            image.setHeight(imgHeight);

            // EXIF信息
            image.setCameraModel(cameraModel);
            image.setTakenTime(takenTime);
            image.setLatitude(latitude);
            image.setLongitude(longitude);

            // === 5. 插入数据库 ===
            imageMapper.insertImage(image);

            System.out.println("=== 图片上传完成 ===");
            System.out.println("原图保存位置: " + originalPath);
            System.out.println("缩略图位置: " + thumbnailPath);
            System.out.println("图片尺寸: " + imgWidth + "x" + imgHeight);
            if (cameraModel != null) {
                System.out.println("相机型号: " + cameraModel);
            }
            if (takenTime != null) {
                System.out.println("拍摄时间: " + takenTime);
            }

        } catch (IOException e) {
            throw new RuntimeException("文件处理失败: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("上传失败: " + e.getMessage());
        }

        return null;
    }
    @Override
    public Path findThumbnailPath(Integer imageId){
        String originalPath = imageMapper.getThumbnailPathByImageId(imageId);

        if (originalPath == null || originalPath.trim().isEmpty()) {
            throw new RuntimeException("未找到图片ID为 " + imageId + " 的原图路径");
        }

        return Paths.get(originalPath);
    }
    @Override
    public Path findOriginalPath(Integer imageId){
        String originalPath = imageMapper.getOriginalPathByImageId(imageId);

        if (originalPath == null || originalPath.trim().isEmpty()) {
            throw new RuntimeException("未找到图片ID为 " + imageId + " 的原图路径");
        }

        return Paths.get(originalPath);
    }
}
