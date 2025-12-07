package com.imageplatform.mapper;

import com.imageplatform.entity.Token;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface TokenMapper {

    Token findById(Integer id);

    List<Token> findAll();

    Token findByUserID(String userId);

    Token findByToken(String token);

    int insert(Token token);

    int update(Token token);

    boolean existsByUsername(String username);
}