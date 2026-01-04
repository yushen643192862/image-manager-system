package com.imageplatform.mapper;

import com.imageplatform.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Integer id);

    List<User> findAll();

    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);
    @Insert("INSERT INTO users(username, password, email, avatar_url) " +
            "VALUES(#{username}, #{password}, #{email}, #{avatarUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Update("UPDATE users SET username = #{username}, avatar_url = #{avatarUrl} WHERE id = #{id}")
    int update(User user);

    int deleteById(Integer id);

    boolean existsByUsername(String username);

    int updateAvatar(@Param("id") Integer id, @Param("avatarUrl") String avatarUrl);
}