package com.loogingko.ncjd.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 手册类别
 * @author Lingma 2026-05-05
 */
@Data
@TableName("manual_category")
@NoArgsConstructor
public class ManualCategory {

    /**
     * 主键ID（雪花算法生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 所属用户（0=公开/登陆可见）
     */
    private String userId;

    /**
     * 类别名称，如「我的观影」
     */
    private String name;

    /**
     * 类别类型，如 movie, tv, book, comic, music, game, oc
     */
    private String type;

    /**
     * 类别描述
     */
    private String memo;

    /**
     * 手动排序，越小越靠前
     */
    private Integer sort;

    /**
     * 显示状态：0=公开 1=登录可见 2=仅自己
     */
    private Integer visible;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}