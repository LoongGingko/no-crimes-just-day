package com.loogingko.ncjd.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * HS256 签名密钥（UTF-8 字节长度须 ≥ 32）
     */
    private String secret;

    /**
     * 过期时间（秒）
     */
    private long expiration;
}
