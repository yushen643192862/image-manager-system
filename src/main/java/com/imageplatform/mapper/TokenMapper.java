package com.imageplatform.mapper;

import com.imageplatform.entity.Token;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.*;
import java.time.LocalDateTime;

import java.util.List;

@Mapper
public interface TokenMapper {

    Token findById(Integer id);

    List<Token> findAll();

    Token findByUserID(String userId);
    @Select("SELECT * FROM user_tokens WHERE token = #{token}")

    Token findByToken(String token);
    @Insert("INSERT INTO user_tokens(user_id, token, device_info, ip_address, expiry_time, is_valid) " +
            "VALUES(#{userId}, #{token}, #{deviceInfo}, #{ipAddress}, #{expiryTime}, #{isValid})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertToken(Token token);

    int update(Token token);

    boolean existsByUsername(String username);
}