package com.imageplatform.mapper;

import com.imageplatform.entity.ResetToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ResetTokenMapper {
    @Select("SELECT * FROM password_reset_tokens WHERE token = #{token} AND used = 0 AND expiry_time > #{now_time}")
    ResetToken findByToken(String token, Integer now_time);

    @Insert("INSERT INTO password_reset_tokens(user_id, token, expiry_time, used) " +
            "VALUES(#{userId}, #{token}, #{expiryTime}, #{used})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertToken(ResetToken token);
}
