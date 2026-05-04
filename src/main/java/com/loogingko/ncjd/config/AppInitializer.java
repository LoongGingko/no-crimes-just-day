package com.loogingko.ncjd.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 应用启动前初始化 (此时Spring容器已经启动，可以安全地使用注入的Bean)
 * @author LiuRunYu 2026-04-11
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AppInitializer {

    private final DataSource dataSource;
    private final StringRedisTemplate redisTemplate;
    
    @PostConstruct
    public void init() {
        log.info("应用启动初始化...");
        // 初始化逻辑
        initDb(); // 测试数据库
        initCache(); // 测试Redis缓存
    }

    // 测试数据库
    private void initDb() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(3)) {
                log.info("数据库连接成功: {}", connection.getMetaData().getURL());
            } else {
                throw new SQLException("数据库连接无效");
            }
        } catch (SQLException e) {
            log.error("数据库连接失败", e);
            System.err.println("❌ 数据库连接失败: " + e.getMessage());
            throw new RuntimeException("数据库连接失败，请检查配置和网络", e);
        }
    }

    // 测试Redis缓存
    private void initCache() {
        try {
            String result = redisTemplate.execute(RedisConnection::ping);
            if ("PONG".equals(result)) {
                log.info("Redis连接成功: {}", result);
            } else {
                throw new RuntimeException("Redis响应异常: " + result);
            }
        } catch (Exception e) {
            log.error("Redis连接失败", e);
            System.err.println("❌ Redis连接失败: " + e.getMessage());
            throw new RuntimeException("Redis连接失败，请检查配置和网络", e);
        }
    }
}
