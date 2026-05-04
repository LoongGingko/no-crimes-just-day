package com.loogingko.ncjd.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 防暴力破解服务
 * 基于 Redis 实现登录/注册频率限制
 * @author LiuRunYu 2026-04-14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LimitService {

    private final StringRedisTemplate redisTemplate;

    private static final String LOGIN_LIMIT_PREFIX = "rate:login:"; // 登录限制
    private static final String REGISTER_LIMIT_PREFIX = "rate:register:"; // 注册限制

    private static final int LOGIN_MAX_ATTEMPTS = 5; // 最大登录次数
    private static final int REGISTER_MAX_ATTEMPTS = 3; // 最大注册次数
    private static final long LOCK_DURATION_MINUTES = 15;
    private static final long WINDOW_SECONDS = 300; // 5分钟后重试

    /* ================ 登录频率检查 ================================================== */
    // 检查登录频率限制 (true=已被限制，false=允许继续)
    public boolean isLoginBlocked(String identifier) {
        return isBlocked(LOGIN_LIMIT_PREFIX, identifier, LOGIN_MAX_ATTEMPTS);
    }

    // 记录登录失败
    public void recordLoginFailure(String identifier) {
        addCount(LOGIN_LIMIT_PREFIX, identifier, WINDOW_SECONDS);
    }

    // 清除登录限制记录（登录成功后调用）
    public void clearLoginLimit(String identifier) {
        redisTemplate.delete(LOGIN_LIMIT_PREFIX + identifier);
    }

    /* ================ 注册频率检查 ================================================== */
    // 检查注册频率限制 (true=已被限制，false=允许继续)
    public boolean isRegisterBlocked(String identifier) {
        return isBlocked(REGISTER_LIMIT_PREFIX, identifier, REGISTER_MAX_ATTEMPTS);
    }

    // 记录注册尝试
    public void recordRegisterAttempt(String identifier) {
        addCount(REGISTER_LIMIT_PREFIX, identifier, WINDOW_SECONDS);
    }

    /**
     * 获取剩余锁定时间（秒）
     * @param keyPrefix Redis键前缀
     * @param identifier 用户标识
     * @return 剩余秒数，0表示未锁定
     */
    public long getRemainingTime(String keyPrefix, String identifier) {
        String key = keyPrefix + identifier;
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ttl != null && ttl > 0 ? ttl : 0;
    }

    // 检查是否被限制
    private boolean isBlocked(String keyPrefix, String identifier, int maxAttempts) {
        String key = keyPrefix + identifier;
        String value = redisTemplate.opsForValue().get(key);
        int attempts = value != null ? Integer.parseInt(value) : 0;

        if (attempts >= maxAttempts) {
            long remainingTime = getRemainingTime(keyPrefix, identifier);
            log.warn("频率限制触发: key={}, attempts={}, remaining={}s", key, attempts, remainingTime);
            return true; // 已被限制
        }
        return false; // 允许继续
    }

    // 递增计数器
    private void addCount(String keyPrefix, String identifier, long expireSeconds) {
        String key = keyPrefix + identifier;
        redisTemplate.opsForValue().increment(key); // 计数器加1
        redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS); // 重置TTL
    }
}