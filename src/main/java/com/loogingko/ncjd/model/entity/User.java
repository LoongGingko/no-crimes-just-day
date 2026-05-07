package com.loogingko.ncjd.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.loogingko.ncjd.constant.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统用户
 * @author LiuRunYu 2026-04-07
 */
@Data
@TableName("sys_user")
@NoArgsConstructor
public class User {

    // 注册构造器
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.nickname = "新注册用户";
        this.status = 1;
        this.setLoginTime(LocalDateTime.now()); // 登录时间
        this.setStatus(Status.ENABLED.getCode());  // 启用状态
    }

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String mobile;
    private String email;
    private String role;
    private Integer status;
    private LocalDateTime loginTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
