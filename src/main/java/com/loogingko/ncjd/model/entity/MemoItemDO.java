package com.loogingko.ncjd.model.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 笔记条目
 * @author @author LiuRunYu 2026-06-08
 */
@Data
@TableName("memo_item")
@NoArgsConstructor
public class MemoItemDO {

    /**
     * 主键ID（雪花算法生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 所属用户（公开/登录可见时默认值为0）
     */
    private String userId;

    /**
     * 笔记内容，支持HTML或Markdown富文本
     */
    private String content;

    /**
     * 笔记纯文本，用于全文检索或预览（去除HTML标签）
     */
    private String plainText;

    /**
     * 标签，存储时首尾加逗号，如 ,待办,金句,经验,
     */
    private String tags;

    /**
     * 是否置顶：0=否, 1=是
     */
    private Integer isPinned;

    /**
     * 是否首页横幅：0=否, 1=是
     */
    private Integer isBanner;

    /**
     * 来源设备信息，如: iOS, Web, Chrome Plugin
     */
    private String device;

    /**
     * 显示状态：0=公开 1=登录可见 2=仅自己
     */
    private Integer visible;

    /**
     * 创建时间
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createAt;

    /**
     * 更新时间
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime updateAt;

    /**
     * 软删除时间，NULL表示未删除，有值表示已删除
     */
    private LocalDateTime deletedAt;
}
