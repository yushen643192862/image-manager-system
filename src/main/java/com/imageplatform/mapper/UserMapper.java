package com.imageplatform.mapper;

import com.imageplatform.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {

    User findById(Integer id);

    List<User> findAll();

    User findByEmail(String email);

    int insert(User user);

    int update(User user);

    int deleteById(Integer id);

    boolean existsByUsername(String username);

    int updateAvatar(@Param("id") Integer id, @Param("avatarUrl") String avatarUrl);
}