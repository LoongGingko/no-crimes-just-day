package com.loogingko.ncjd;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.junit.jupiter.api.Test;

/**
 * 雪花算法ID生成测试类
 * @author LiuRunYu 2026-05-05
 */
public class SnowflakeTest {

    /**
     * 使用MyBatis-Plus，生成n个雪花ID 
     */
    @Test
    void generateSnowflakeIds() {
        System.out.println("=== 开始生成n个雪花算法ID ===");

        for (int i = 1; i <= 50; i++) {
            long id = IdWorker.getId();
            System.out.println("第" + i + "个ID: " + id);
        }

        System.out.println("=== ID生成完毕 ===");
    }
}