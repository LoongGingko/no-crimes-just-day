package com.loogingko.ncjd.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 手册条目
 * @author Lingma 2026-05-05
 */
@Data
@TableName("manual_item")
@NoArgsConstructor
public class ManualItem {

    /**
     * 主键ID（雪花算法生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 所属用户（0=公开/登录可见）
     */
    private String userId;

    /**
     * 所属类别
     */
    private String cateId;

    /**
     * 条目名称，如电影名、书名
     */
    private String title;

    /**
     * 封面图片URL
     */
    private String cover;

    /**
     * 标签，存储时首尾加逗号，如 ,科幻,经典,诺兰,
     */
    private String tags;

    /**
     * 个人评论
     */
    private String comment;

    /**
     * 个人评分 (0~100)
     */
    private Integer rating;

    /**
     * 元信息，如 {"year":2020,"director":"诺兰"}
     */
    private String meta;

    /**
     * 开始时间
     */
    private LocalDateTime startedAt;

    /**
     * 结束时间
     */
    private LocalDateTime finishedAt;

    /**
     * 进度 (0~100)，NULL=待办
     */
    private Integer progress;

    /**
     * 手动排序，越小越靠前
     */
    private Integer sort;

    /**
     * 显示状态：0=公开 1=登录可见 2=仅自己
     */
    private Integer visible;

    /**
     * 完成状态：0=待办 1=进行中 2=已完成
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}