package com.imageplatform.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Tag {
    private Integer id;
    private String name;
    private Integer createdBy;
    private LocalDateTime createdTime;
    private Integer number;
}