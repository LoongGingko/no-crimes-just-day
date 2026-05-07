package com.loogingko.ncjd.model.vo;

import lombok.Data;

/**
 * 登录成功后前端拿到的对象
 * @author LiuRunYu 2026-04-07
 */
@Data
public class LoginResponse {
    private String id;
    private String username;
    private String nickname;

    public LoginResponse() {}

    public LoginResponse(String id, String username, String nickname) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
    }
}
