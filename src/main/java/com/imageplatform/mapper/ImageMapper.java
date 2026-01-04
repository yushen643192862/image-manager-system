package com.imageplatform.mapper;
import com.imageplatform.entity.Image;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ImageMapper {
    @Select("SELECT * FROM image WHERE user_id = #{userid} ORDER BY upload_time DESC")
    List<Image> getImageByUserId(Integer userid);

    @Select("SELECT thumbnail_path FROM image WHERE id = #{imageid}")
    String getOriginalPathByImageId(Integer imageid);

    @Select("SELECT storage_path FROM image WHERE id = #{imageid}")
    String getThumbnailPathByImageId(Integer imageid);

    @Insert("INSERT INTO image (" +
            "user_id, original_filename, storage_path, thumbnail_path, " +
            "file_size, upload_time, title, width, height, " +
            "camera_model, taken_time, latitude, longitude" +
            ") VALUES (" +
            "#{userId}, #{originalFilename}, #{storagePath}, #{thumbnailPath}, " +
            "#{fileSize}, #{uploadTime}, #{title}, #{width}, #{height}, " +
            "#{cameraModel}, #{takenTime}, #{latitude}, #{longitude}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insertImage(Image image);
}
