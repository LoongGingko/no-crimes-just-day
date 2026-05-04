package com.loogingko.ncjd.service;

import com.loogingko.ncjd.config.JwtProperties;
import com.loogingko.ncjd.model.bo.LoginUser;
import com.loogingko.ncjd.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author LiuRunYu 2026-04-07
 */
@Service
public class JwtService {

    private final JwtProperties properties;
    private final SecretKey secretKey;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.secretKey = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    // 创建JWT令牌
    public String createToken(User user) {
        // 1. 获取当前时间
        long now = System.currentTimeMillis();
        // 2. 获取过期时间
        long exp = now + properties.getExpiration() * 1000;
        // 3. 创建JWT令牌
        return Jwts.builder()
                .claim("id", user.getId())
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .issuedAt(new Date(now)) // 签发时间
                .expiration(new Date(exp)) // 过期时间
                .signWith(secretKey) // 签名
                .compact(); // 返回JWT令牌
    }

    // 校验令牌；无效或过期时抛出 {@link JwtException}。
    public void assertValid(String token) throws JwtException {
        Jwts.parser() // 解析JWT令牌
                .verifyWith(secretKey)
                .build() // 构建解析器
                .parseSignedClaims(token);
    }

    // 校验令牌：并提取用户信息 (令牌无效或过期时抛出 JwtException)
    public LoginUser getUserFromToken(String token) throws JwtException {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return new LoginUser(
            claims.get("id", String.class), 
            claims.get("username", String.class),
            claims.get("role", String.class)
        );
    }
}
