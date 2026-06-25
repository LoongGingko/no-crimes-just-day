package com.loogingko.ncjd.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class MemoListReq {
    
    // 是否删除(1=是 0=否)
    private Integer delFlag;
    
    // 模糊查询
    private String keyword;
    
    // 标签
    private List<String> tags;
    
    // 起始/结束时间 (按创建时间查询，提供预设值)
    private String dateStart;
    private String dateEnd;
}

