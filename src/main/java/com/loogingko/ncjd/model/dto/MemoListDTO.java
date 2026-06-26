package com.loogingko.ncjd.model.dto;

import lombok.Data;

@Data
public class MemoListDTO {
    
    private String id;

    private String userId;

    private String content;

    private String plainText;

    private String tags;

    private Integer isPinned;

    private Integer isBanner;

    // 展示时间 (刚刚、X分钟前、X小时前、X天前、M月d日...)
    private String dateShow;
}

