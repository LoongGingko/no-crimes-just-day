-- created by LiuRunYu 2026-04-07

-- 创建数据库
create schema ncjd collate utf8mb4_unicode_ci;

CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL COMMENT '主键ID（雪花算法生成）',
    `username` VARCHAR(20) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';


-- 插入 admin 账号（启用状态，密码为 123456 的 BCrypt 加密值）
INSERT INTO sys_user (id, username, password, nickname, avatar, status, create_time, update_time)
VALUES (1694173827450880001, 'admin', 'bcrypt_password', '系统管理员', NULL, 1, NOW(), NOW());
-- 插入测试员账号 tester（启用状态，密码为 123456 的 BCrypt 加密值）
INSERT INTO sys_user (id, username, password, nickname, avatar, status, create_time, update_time)
VALUES (1694173827450880002, 'tester', 'bcrypt_password', '测试人员', NULL, 1, NOW(), NOW());