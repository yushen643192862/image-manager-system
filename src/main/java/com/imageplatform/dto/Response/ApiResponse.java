package com.imageplatform.dto.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.io.Serializable;

/**
 * 通用API响应包装类
 * @param <T> 数据类型
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)  // 为空时不序列化
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    // 成功状态码
    public static final Integer SUCCESS_CODE = 200;
    // 失败状态码
    public static final Integer ERROR_CODE = 400;
    // 未认证状态码
    public static final Integer UNAUTHORIZED_CODE = 401;
    // 禁止访问
    public static final Integer FORBIDDEN_CODE = 403;
    // 未找到
    public static final Integer NOT_FOUND_CODE = 404;
    // 服务器错误
    public static final Integer SERVER_ERROR_CODE = 500;

    private Integer code;       // 状态码
    private String message;     // 消息
    private T data;            // 数据
    private Long timestamp;     // 时间戳
    private String path;        // 请求路径（可选）
    private String requestId;   // 请求ID（用于追踪）

    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(Integer code, String message, T data, String path) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.path = path;
        this.timestamp = System.currentTimeMillis();
    }

    /* ========== 成功响应 ========== */

    public static <T> ApiResponse<T> success() {
        return success("操作成功", null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return success("操作成功", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(SUCCESS_CODE, message, data);
    }

    /* ========== 失败响应 ========== */

    public static <T> ApiResponse<T> error() {
        return error("操作失败");
    }

    public static <T> ApiResponse<T> error(String message) {
        return error(ERROR_CODE, message);
    }

    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public static <T> ApiResponse<T> error(Integer code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    /* ========== 特定状态响应 ========== */

    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(UNAUTHORIZED_CODE, message, null);
    }

    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(FORBIDDEN_CODE, message, null);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(NOT_FOUND_CODE, message, null);
    }

    public static <T> ApiResponse<T> serverError(String message) {
        return new ApiResponse<>(SERVER_ERROR_CODE, message, null);
    }

    /* ========== 链式调用支持 ========== */

    public ApiResponse<T> code(Integer code) {
        this.code = code;
        return this;
    }

    public ApiResponse<T> message(String message) {
        this.message = message;
        return this;
    }

    public ApiResponse<T> data(T data) {
        this.data = data;
        return this;
    }

    public ApiResponse<T> path(String path) {
        this.path = path;
        return this;
    }

    public ApiResponse<T> requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }
}