package com.imageplatform.controller;

import com.imageplatform.common.exception.authexception.*;
import com.imageplatform.dto.Request.ForgetRequest;
import com.imageplatform.dto.Response.ApiResponse;
import com.imageplatform.dto.Request.LoginRequest;
import com.imageplatform.dto.Request.RegisterRequest;
import com.imageplatform.dto.Response.LoginResponse;
import com.imageplatform.dto.Response.RegisterResponse;
import com.imageplatform.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController  // 表明这是REST控制器
@RequestMapping("/api/auth")  // 基础路径
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired  // 自动注入Service
    private AuthService authService;

    /**
     * 用户登录接口
     * POST http://localhost:8081/api/auth/login
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {

        String ipAddress = httpRequest.getRemoteAddr();

        String userAgent = httpRequest.getHeader("User-Agent");
        try {
            LoginResponse response = authService.login(request, ipAddress, userAgent);
            return ApiResponse.success("登录成功", response);
        } catch (RuntimeException e) {
            return ApiResponse.error(2001, e.getMessage());
        }
    }

    /**
     * token登录接口
     * POST http://localhost:8081/api/auth/token
     */
    @PostMapping("/token")
    public ApiResponse<LoginResponse> login(@RequestHeader(value = "Authorization", required = false) String authHeader, HttpServletRequest httpRequest) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ApiResponse.error(400, "令牌不能为空");
        }

        String token = authHeader.substring(7);

        String ipAddress = httpRequest.getRemoteAddr();

        String userAgent = httpRequest.getHeader("User-Agent");

        try {
            LoginResponse response = authService.tokenLogin(token, ipAddress, userAgent);
            return ApiResponse.success("登录成功", response);
        } catch (TokenNotFoundException e) {
            return ApiResponse.error(1001, "令牌不存在，请重新登录");

        } catch (DeviceMismatchException e) {
            return ApiResponse.error(1002, "设备不匹配，请使用原设备登录");

        } catch (TokenExpiredException e) {
            return ApiResponse.error(1003, "登录已过期，请重新登录");

        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    /**
     * 用户注册接口
     * POST http://localhost:8081/api/auth/register
     */
    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = authService.register(request);
            return ApiResponse.success("注册成功", response);
        } catch (UsernameAlreadyExistsException e) {
            return ApiResponse.error(1004, e.getMessage());
        } catch (EmailAlreadyExistsException e) {
            return ApiResponse.error(1005, e.getMessage());
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
}