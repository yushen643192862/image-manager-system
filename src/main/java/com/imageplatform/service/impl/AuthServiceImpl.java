package com.imageplatform.service.impl;

import com.imageplatform.common.exception.authexception.*;
import com.imageplatform.common.utils.JwtTokenUtil;
import com.imageplatform.dto.Request.ForgetRequest;
import com.imageplatform.dto.Request.RegisterRequest;
import com.imageplatform.dto.Response.LoginResponse;
import com.imageplatform.dto.Response.RegisterResponse;
import com.imageplatform.entity.Token;
import com.imageplatform.entity.User;
import com.imageplatform.mapper.ResetTokenMapper;
import com.imageplatform.service.AuthService;
import com.imageplatform.dto.Request.LoginRequest;
import com.imageplatform.mapper.UserMapper;
import com.imageplatform.mapper.TokenMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserMapper userMapper, JwtTokenUtil jwtTokenUtil, TokenMapper tokenMapper, ResetTokenMapper resetTokenMapper) {
        this.userMapper = userMapper;
        this.jwtTokenUtil = jwtTokenUtil;
        this.tokenMapper = tokenMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public LoginResponse login(LoginRequest request, String ip, String device) {


        User user = userMapper.findByEmail(request.getEmail());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        LoginResponse loginResponse = new LoginResponse();
        String token_str;
        token_str = jwtTokenUtil.generateToken(user.getId().longValue(), user.getEmail(), user.getUsername(), request.isRememberMe());
        Token token = new Token();
        token.setUserId(user.getId());
        token.setToken(token_str);
        token.setDeviceInfo(device);
        token.setIpAddress(ip);
        token.setExpiryTime(jwtTokenUtil.getExpirationDateFromToken(token_str).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        token.setValid(true);
        token.setValid(token.isValidToken());
        loginResponse.setToken(token_str);
        loginResponse.setAvatarUrl(user.getAvatarUrl());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setEmail(user.getEmail());
        loginResponse.setUserId(user.getId());
        tokenMapper.insertToken(token);
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
        if (!token.getDeviceInfo().equals(device)) {
            throw new DeviceMismatchException("设备信息不匹配，请重新登录");
        }
        if (!token.isValid()) {
            throw new TokenExpiredException();
        }
        User user = userMapper.findById(token.getUserId());
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setEmail(user.getEmail());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setToken(token.getToken());
        loginResponse.setAvatarUrl(user.getAvatarUrl());
        loginResponse.setUserId(user.getId());
        return loginResponse;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {

        User user = userMapper.findByEmail(request.getEmail());
        if (!(user == null)) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }
        user = userMapper.findByUsername(request.getUsername());
        if (!(user == null)) {
            throw new UsernameAlreadyExistsException(user.getUsername());
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        String password = passwordEncoder.encode(request.getPassword());
        user = new User();
        user.setPassword(password);
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setAvatarUrl(null);
        userMapper.insert(user);
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setEmail(request.getEmail());
        registerResponse.setUsername(request.getUsername());
        registerResponse.setAvatarUrl(null);
        return registerResponse;
    }
}