package com.loogingko.ncjd.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录用户的上下文信息
 * @author LiuRunYu 2026-04-07
 */
@Data
@NoArgsConstructor
public class LoginUserDTO {
    private String id; // 用户ID
    private String username; // 账号
    private String role; // 角色
    private String tokenId; // 令牌Redis白名单（暂未用到）

    public LoginUserDTO(String id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
}
