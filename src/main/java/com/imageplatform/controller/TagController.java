package com.imageplatform.controller;

import com.imageplatform.dto.Request.TagCreateRequest;
import com.imageplatform.dto.Request.TagDeleteRequest;
import com.imageplatform.dto.Response.ApiResponse;
import com.imageplatform.dto.Response.TagGetResponse;
import com.imageplatform.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@RestController  // 表明这是REST控制器
@RequestMapping("/api/tag")  // 基础路径
@CrossOrigin(origins = "http://localhost:5173")
public class TagController {
    @Autowired  // 自动注入Service
    private TagService tagservice;

    /**
     * 标签获取接口
     * POST http://localhost:8081/api/tag/get?userId=xxx
     */
    @GetMapping("/get")
    public ApiResponse<TagGetResponse> getTag(@RequestParam Integer userId){
        try {
            TagGetResponse response = tagservice.getTag(userId);
            return ApiResponse.success("申请成功", response);
        }
        catch (RuntimeException e) {
            return ApiResponse.error(2001, e.getMessage());
        }
    }

    /**
     * 标签创建接口
     * Post http://localhost:8081/api/tag/create
     */
    @PostMapping("/create")
    public ApiResponse<LocalDateTime> createTag(@RequestBody TagCreateRequest request){
        try {
            LocalDateTime date = tagservice.createTag(request);
            return ApiResponse.success("创建成功", date);
        }
        catch (RuntimeException e) {
            return ApiResponse.error(2001, e.getMessage());
        }
    }

    /**
     * 标签删除接口
     * Delete http://localhost:8081/api/tag/delete
     */
    @DeleteMapping("/delete")
    public ApiResponse<Void> getTag(@RequestBody TagDeleteRequest request){
        try {
            tagservice.deleteTag(request);
            return ApiResponse.success("删除成功", null);
        }
        catch (RuntimeException e) {
            return ApiResponse.error(2001, e.getMessage());
        }
    }
}

