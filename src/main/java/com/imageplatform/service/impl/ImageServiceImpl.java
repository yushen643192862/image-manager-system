package com.imageplatform.service.impl;

import com.imageplatform.dto.Request.GetThumbnailImageRequest;
import com.imageplatform.dto.Request.UpdateImageInforRequest;
import com.imageplatform.dto.Response.GetOriginalImageResponse;
import com.imageplatform.dto.Response.GetThumbnailImageResponse;
import com.imageplatform.dto.Response.ReturnImg;
import com.imageplatform.entity.Image;
import com.imageplatform.entity.ImageTag;
import com.imageplatform.entity.Tag;
import com.imageplatform.mapper.ImageMapper;
import com.imageplatform.mapper.ImageTagMapper;
import com.imageplatform.mapper.TagMapper;
import com.imageplatform.service.ImageService;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;

import org.springframework.beans.factory.annotation.Value;
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
    private final TagMapper tagMapper;
    private final ImageTagMapper imageTagMapper;

    public ImageServiceImpl(ImageMapper imageMapper, TagMapper tagMapper,  ImageTagMapper imageTagMapper) {
        this.imageMapper = imageMapper;
        this.tagMapper = tagMapper;
        this.imageTagMapper = imageTagMapper;
    }

    @Value("${app.upload.image-dir:/app/uploads/images}")
    private String imageUploadDir;

    @Value("${app.upload.thumbnail-dir:/app/uploads/thumbnails}")
    private String thumbnailDir;

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
            String originalFilename = file.getOriginalFilename();
            String originalDir = imageUploadDir;
            Files.createDirectories(Paths.get(originalDir));

            String uuid = UUID.randomUUID().toString();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String originalFileName = uuid + fileExtension;
            Path originalPath = Paths.get(originalDir + originalFileName);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, originalPath, StandardCopyOption.REPLACE_EXISTING);
            }

            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            int imgWidth = originalImage.getWidth();
            int imgHeight = originalImage.getHeight();

            String thumbnailDir = this.thumbnailDir;
            Files.createDirectories(Paths.get(thumbnailDir));

            String thumbnailFileName = "thumb_" + uuid + ".jpg";
            Path thumbnailPath = Paths.get(thumbnailDir + thumbnailFileName);

            Thumbnails.of(originalPath.toFile())
                    .width(300)
                    .keepAspectRatio(true)
                    .outputQuality(0.9)
                    .outputFormat("jpg")
                    .toFile(thumbnailPath.toFile());

            String cameraModel = null;
            LocalDateTime takenTime = null;
            Float latitude = null;
            Float longitude = null;

            try {
                ImageMetadata metadata = Imaging.getMetadata(file.getBytes());

                if (metadata != null && metadata instanceof JpegImageMetadata) {
                    JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

                    TiffImageMetadata exif = jpegMetadata.getExif();
                    if (exif != null) {
                        Object modelValue = exif.getFieldValue(
                                org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants.TIFF_TAG_MODEL);
                        if (modelValue != null) {
                            cameraModel = modelValue.toString();
                        }

                        Object dateValue = exif.getFieldValue(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
                        if (dateValue != null) {
                            String dateStr = dateValue.toString();
                            try {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
                                takenTime = LocalDateTime.parse(dateStr, formatter);
                            } catch (Exception e) {
                                // 日期解析失败
                            }
                        }

                        TiffImageMetadata.GPSInfo gpsInfo = exif.getGPS();
                        if (gpsInfo != null) {
                            latitude = (float) gpsInfo.getLatitudeAsDegreesNorth();
                            longitude = (float) gpsInfo.getLongitudeAsDegreesEast();
                        }
                    }
                }
            } catch (ImageReadException | IOException e) {
                // EXIF信息读取失败
            }

            Image image = new Image();
            image.setUserId(userId);
            image.setOriginalFilename(originalFilename);
            image.setStoragePath(originalPath.toString());
            image.setThumbnailPath(thumbnailPath.toString());
            image.setFileSize((int) file.getSize());
            image.setUploadTime(LocalDateTime.now());
            image.setWidth(imgWidth);
            image.setHeight(imgHeight);
            image.setCameraModel(cameraModel);
            image.setTakenTime(takenTime);
            image.setLatitude(latitude);
            image.setLongitude(longitude);

            imageMapper.insertImage(image);

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

    @Override
    public GetOriginalImageResponse getOriginalDetail(Integer imageId){
        Image image = imageMapper.getImageByImageId(imageId);

        if (image == null) {
            return null;
        }

        List<ImageTag> imageTags = imageTagMapper.getImageTagsByImageId(imageId);
        List<Tag> tags = new ArrayList<>();
        for (ImageTag imageTag : imageTags) {
            Tag tag = tagMapper.getTagById(imageTag.getTagId());
            tags.add(tag);
        }

        GetOriginalImageResponse response = new GetOriginalImageResponse();
        response.setImageId(image.getId());
        response.setTitle(image.getTitle());
        response.setDescription(image.getDescription());
        response.setSize(image.getFileSize());
        response.setUploadTime(image.getUploadTime());
        response.setWidth(image.getWidth());
        response.setHeight(image.getHeight());
        response.setCameraModel(image.getCameraModel());
        response.setTakenTime(image.getTakenTime());
        response.setLatitude(image.getLatitude());
        response.setLongitude(image.getLongitude());
        response.setTags(tags);

        return response;
    }

    @Override
    public Void updateImageinfor(Integer imageId, UpdateImageInforRequest request){
        imageMapper.updateImageByImageID(imageId, request.getTitle(), request.getDescription());
        imageTagMapper.deleteImageTagByImageId(imageId);
        if(request.getTags() != null){
            for(Integer tagid : request.getTags()){
                ImageTag imageTag = new ImageTag();
                imageTag.setTagId(tagid);
                imageTag.setImageId(imageId);
                imageTagMapper.insertImageTag(imageTag);
            }
        }
        return null;
    }
    @Override
    public Void updateImage(Integer imageId, MultipartFile file) {
        try {
            // 1. 从数据库查询原图片信息
            Image oldImage = imageMapper.getImageByImageId(imageId);
            if (oldImage == null) {
                throw new RuntimeException("图片不存在，ID: " + imageId);
            }

            // 2. 直接使用数据库中的文件路径（不变）
            String originalFilePath = oldImage.getStoragePath();
            String thumbnailFilePath = oldImage.getThumbnailPath();

            if (originalFilePath == null || thumbnailFilePath == null) {
                throw new RuntimeException("图片文件路径不存在");
            }

            Path originalPath = Paths.get(originalFilePath);
            Path thumbnailPath = Paths.get(thumbnailFilePath);

            // 3. 确保目录存在
            Files.createDirectories(originalPath.getParent());

            // 4. 直接覆盖原文件（保持路径不变）
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, originalPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // 5. 获取新文件的属性
            long fileSize = file.getSize();
            int width = 0;
            int height = 0;
            try {
                BufferedImage bufferedImage = ImageIO.read(originalPath.toFile());
                if (bufferedImage != null) {
                    width = bufferedImage.getWidth();
                    height = bufferedImage.getHeight();
                }
            } catch (Exception e) {
                throw new RuntimeException("获取新文件的属性失败: " + e.getMessage());
            }

            // 6. 重新生成缩略图（覆盖原缩略图）
            Thumbnails.of(originalPath.toFile())
                    .width(300)
                    .keepAspectRatio(true)
                    .outputQuality(0.9)
                    .outputFormat("jpg")
                    .toFile(thumbnailPath.toFile());

            // 7. 只更新数据库中的大小、宽高信息（文件路径不变）
            if (fileSize > 0 && width > 0 && height > 0) {
                try {
                    imageMapper.updateImageByImageID_S_H_W(
                            imageId,
                            (int) fileSize,
                            width,
                            height
                    );
                } catch (Exception e) {
                    throw new RuntimeException("更新数据库失败: " + e.getMessage());
                }
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException("文件处理失败: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("更新图片失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Void deleteImage(Integer imageId){
        imageTagMapper.deleteImageTagByImageId(imageId);
        imageMapper.deleteImageByImageId(imageId);
        return null;
    }
}