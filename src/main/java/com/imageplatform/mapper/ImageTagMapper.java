package com.imageplatform.mapper;

import com.imageplatform.entity.ImageTag;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ImageTagMapper {

    /**
     * 根据标签ID删除图片标签关联关系
     * @param tagId 标签ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM image_tag WHERE tag_id = #{tagId}")
    int deleteImageTagByTagId(@Param("tagId") Integer tagId);

    /**
     * 根据图片ID删除图片标签关联关系
     * @param imageId 图片ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM image_tag WHERE image_id = #{imageId}")
    int deleteImageTagByImageId(@Param("imageId") Integer imageId);

    /**
     * 插入图片标签关联
     * @param imageTag 图片标签对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO image_tag(image_id, tag_id) VALUES(#{imageId}, #{tagId})")
    int insertImageTag(ImageTag imageTag);

    /**
     * 根据图片ID查询标签关联
     * @param imageId 图片ID
     * @return 图片标签列表
     */
    @Select("SELECT id, image_id, tag_id FROM image_tag WHERE image_id = #{imageId}")
    List<ImageTag> getImageTagsByImageId(@Param("imageId") Integer imageId);

    /**
     * 根据标签ID查询图片关联
     * @param tagId 标签ID
     * @return 图片标签列表
     */
    @Select("SELECT id, image_id, tag_id FROM image_tag WHERE tag_id = #{tagId}")
    List<ImageTag> getImageTagsByTagId(@Param("tagId") Integer tagId);

    @Select("SELECT COUNT(*) FROM image_tag WHERE tag_id = #{tagId}")
    int getCountByTagId(@Param("tagId") Integer tagId);
}