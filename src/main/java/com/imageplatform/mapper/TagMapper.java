package com.imageplatform.mapper;

import com.imageplatform.entity.ResetToken;
import com.imageplatform.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TagMapper {

    /**
     * 根据用户ID查询标签列表
     * @param userId 用户ID
     * @return 标签列表
     */
    @Select("SELECT * FROM tags WHERE created_by = #{userId}")
    List<Tag> getTagsByUserId(@Param("userId") Integer userId);

    /**
     * 根据标签ID删除标签
     * @param tagId 标签ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM tags WHERE id = #{tagId}")
    int deleteTagByTagId(@Param("tagId") Integer tagId);

    /**
     * 插入新标签
     * @param tag 标签对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO tags(name, created_by,created_time) VALUES(#{name}, #{createdBy}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertTag(Tag tag);

    /**
     * 根据ID查询标签
     * @param id 标签ID
     * @return 标签对象
     */
    @Select("SELECT id, name, created_by FROM tags WHERE id = #{id}")
    Tag getTagById(@Param("id") Integer id);


}