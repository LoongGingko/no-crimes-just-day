package com.loogingko.ncjd.model.bo;

/**
 * 登录用户的上下文信息
 * @author LiuRunYu 2026-04-07
 */
public class LoginUser {
    private String id; // 用户ID
    private String username; // 账号
    private String role; // 角色
    private String tokenId; // 令牌Redis白名单（暂未用到）

    public LoginUser() {}

    public LoginUser(String id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }
}
