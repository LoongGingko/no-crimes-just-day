package com.loogingko.ncjd.model.vo;

import com.loogingko.ncjd.model.entity.ManualItemDO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 手册视图对象（类别嵌套条目）
 * @author Lingma 2026-05-05
 */
@Data
@NoArgsConstructor
public class ManualVO {
    private List<ManualItemDO> items; // 手册条目列表
    private String id;
    private String userId;
    private String name;
    private String type;
    private String memo;
    private Integer sort;
    private Integer visible;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
