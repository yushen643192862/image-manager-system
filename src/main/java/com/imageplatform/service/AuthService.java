package com.imageplatform.service;

import com.imageplatform.dto.Response.LoginResponse;
import com.imageplatform.dto.Request.LoginRequest;

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
     */
//    void register(RegisterRequest request);
//
//    /**
//     * 用户登出
//     * @param token 用户的token
//     */
//    void logout(String token);
//
//    /**
//     * 刷新访问令牌
//     * @param refreshToken 刷新令牌
//     * @return 新的登录响应
//     */
//    LoginResponse refreshToken(String refreshToken);
//
//    /**
//     * 验证token是否有效
//     * @param token 要验证的token
//     * @return 是否有效
//     */
//    boolean validateToken(String token);
//
//    /**
//     * 根据token获取用户ID
//     * @param token 用户token
//     * @return 用户ID
//     */
//    Integer getUserIdFromToken(String token);
}