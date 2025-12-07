package com.imageplatform.service.impl;

import com.imageplatform.common.exception.DeviceMismatchException;
import com.imageplatform.common.exception.TokenExpiredException;
import com.imageplatform.common.exception.TokenNotFoundException;
import com.imageplatform.common.utils.JwtTokenUtil;
import com.imageplatform.dto.Response.LoginResponse;
import com.imageplatform.entity.Token;
import com.imageplatform.entity.User;
import com.imageplatform.service.AuthService;
import com.imageplatform.dto.Request.LoginRequest;
import com.imageplatform.mapper.UserMapper;
import com.imageplatform.mapper.TokenMapper;
import org.springframework.stereotype.Service;

import java.time.ZoneId;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final TokenMapper tokenMapper;

    public AuthServiceImpl(UserMapper userMapper, JwtTokenUtil jwtTokenUtil, TokenMapper tokenMapper) {
        this.userMapper = userMapper;
        this.jwtTokenUtil = jwtTokenUtil;
        this.tokenMapper = tokenMapper;
    }

    @Override
    public LoginResponse login(LoginRequest request, String ip, String device) {

        LoginResponse loginResponse = new LoginResponse();
        User user = userMapper.findByEmail(request.getEmail());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        else{
            String token_str;
            token_str = jwtTokenUtil.generateToken(user.getId().longValue(),user.getEmail(), user.getUsername(), request.isRememberMe());
            loginResponse.setToken(token_str);
            loginResponse.setAvatarUrl(user.getAvatarUrl());
            Token token = new Token();
            token.setUserId(user.getId());
            token.setToken(token_str);
            token.setDeviceInfo(device);
            token.setIpAddress(ip);
            token.setExpiryTime(jwtTokenUtil.getExpirationDateFromToken(token_str).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            token.setValid(true);
            token.setValid(token.isValidToken());
            tokenMapper.insert(token);
        }

        return loginResponse;
    }

    @Override
    public LoginResponse tokenLogin(String token_str, String ip, String device) {

        Token token = tokenMapper.findByToken(token_str);
        if (token_str == null || token_str.isBlank()) {
            throw new IllegalArgumentException("令牌不能为空");
        }
        if (token == null) {
            throw new TokenNotFoundException("无效的令牌");
        }
        if(!token.getDeviceInfo().equals(device)){
            throw new DeviceMismatchException("设备信息不匹配，请重新登录");
        }
        if (!token.isValid()){
            throw new TokenExpiredException();
        }
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token.getToken());
        loginResponse.setAvatarUrl(userMapper.findById(token.getUserId()).getAvatarUrl());

        return loginResponse;
    }
}