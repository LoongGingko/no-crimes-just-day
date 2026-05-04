package com.loogingko.ncjd.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用枚举值
 * @author LiuRunYu 2026-04-10
 */
@Getter
@AllArgsConstructor
public enum Status {
    DISABLED(0, "未启用"),
    ENABLED(1, "已启用");
    
    private final Integer code;
    private final String desc;
}