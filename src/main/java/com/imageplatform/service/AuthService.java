package com.imageplatform.service;

import com.imageplatform.dto.Request.ForgetRequest;
import com.imageplatform.dto.Request.RegisterRequest;
import com.imageplatform.dto.Response.LoginResponse;
import com.imageplatform.dto.Request.LoginRequest;
import com.imageplatform.dto.Response.RegisterResponse;
import jakarta.validation.Valid;

/**
 * 认证服务接口
 * 定义所有与用户认证相关的业务方法
 */
public interface AuthService {

    /**
     * 用户登录
     * @param request 登录请求类
     * @return 登录响应（包含token和用户信息）
     */
    LoginResponse login(LoginRequest request, String ip, String device);
    /**
     * 用户登录
     * @param token 用户token
     * @return 登录响应（包含token和用户信息）
     */
    LoginResponse tokenLogin(String token, String ip, String device);


    /**
     * 用户注册
     * @param request 注册请求
     * @return 注册响应（包含用户信息）
     */
    RegisterResponse register(RegisterRequest request);

}