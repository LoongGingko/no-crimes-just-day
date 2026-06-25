package com.loogingko.ncjd.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 笔记保存/更新请求（id为空=新增，id非空=更新）
 * @author LiuRunYu 2026-06-25
 */
@Data
public class MemoSaveReq {

    private String id;

    private String content;

    private String plainText;

    private List<String> tags;

    private Integer isPinned;

    private Integer isBanner;

    private String device;

    private Integer visible;
}
