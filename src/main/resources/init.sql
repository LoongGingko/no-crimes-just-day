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


-- ================================================
-- 手册模块建表
-- LiuRunYu 2026-05-05
-- Engine: InnoDB | Charset: utf8mb4
-- ================================================
-- 手册类别表
CREATE TABLE manual_category
(
    id          BIGINT                                      NOT NULL COMMENT '主键ID（雪花算法生成）',
    user_id     BIGINT                                      NOT NULL COMMENT '所属用户（0=公开/登陆可见）',
    name        VARCHAR(64)                                 NOT NULL COMMENT '类别名称，如「我的观影」',
    type        VARCHAR(32)                                 NOT NULL COMMENT '类别类型，如 movie, tv, book, comic, music, game, oc',
    memo        TEXT                                        NULL COMMENT '类别描述',
    sort        INT                                         DEFAULT 0 NOT NULL COMMENT '手动排序，越小越靠前',
    visible     INT                                         NOT NULL DEFAULT 0 COMMENT '显示状态：0=公开 1=登录可见 2=仅自己',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (id),
    UNIQUE KEY uk_user_name (user_id, name),
    INDEX idx_user_type (user_id, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '手册条目';

-- 手册条目表
CREATE TABLE manual_item
(
    id          BIGINT NOT NULL COMMENT '主键ID（雪花算法生成）',
    user_id     BIGINT                                      NOT NULL COMMENT '所属用户（0=公开/登陆可见）',
    cate_id     BIGINT                                      NOT NULL COMMENT '所属类别',
    title       VARCHAR(128)                                NOT NULL COMMENT '条目名称，如电影名、书名',
    cover       VARCHAR(512)                                NULL COMMENT '封面图片URL',
    tags        VARCHAR(255)                                NULL COMMENT '标签，存储时首尾加逗号，如 ,科幻,经典,诺兰,',
    comment     TEXT                                        NULL COMMENT '个人评论',
    rating      INT                                         NULL COMMENT '个人评分 (0~100)',
    meta        JSON                                        NULL COMMENT '元信息，如 {"year":2020,"director":"诺兰"}',
    started_at  DATETIME                                    NULL COMMENT '开始时间',
    finished_at DATETIME                                    NULL COMMENT '结束时间',
    progress    INT                                         NULL COMMENT '进度 (0~100)，NULL=待办',
    sort        INT                                         DEFAULT 0 NOT NULL COMMENT '手动排序，越小越靠前',
    visible     INT                                         NOT NULL DEFAULT 0 COMMENT '显示状态：0=公开 1=登录可见 2=仅自己',
    status      INT                                         NOT NULL DEFAULT 0 COMMENT '完成状态：0=待办 1=进行中 2=已完成',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (id),
    UNIQUE KEY uk_user_cate_title (user_id, cate_id, title),
    INDEX idx_cate_id (cate_id),
    INDEX idx_status (status),
    INDEX idx_created_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '手册条目';

-- 测试数据
-- 插入手册类别数据
INSERT INTO manual_category (id, user_id, name, type, memo, sort, visible, create_time, update_time) VALUES
(2051531076388265986, 0, '我的观影', 'movie', '个人收藏的电影列表', 1, 0, '2026-05-05 10:00:00', '2026-05-05 10:00:00'),
(2051531076388265987, 0, '阅读清单', 'book', '想要阅读的书籍列表', 2, 1, '2026-05-05 11:00:00', '2026-05-05 11:00:00'),
(2051531076388265988, 0, '漫画收藏', 'comic', '个人喜欢的漫画作品', 3, 0, '2026-05-05 12:00:00', '2026-05-05 12:00:00'),
(2051531076388265989, 0, '音乐歌单', 'music', '日常听的音乐合集', 4, 0, '2026-05-05 13:00:00', '2026-05-05 13:00:00'),
(2051531076388265990, 0, '游戏清单', 'game', '想玩和已玩的游戏', 5, 2, '2026-05-05 14:00:00', '2026-05-05 14:00:00'),
(2051531076388265991, 0, '原创角色', 'oc', '自己创作的角色设定', 6, 2, '2026-05-05 15:00:00', '2026-05-05 15:00:00');

-- 插入手册条目数据
INSERT INTO manual_item (id, user_id, cate_id, title, cover, tags, comment, rating, meta, started_at, finished_at, progress, sort, visible, status, create_time, update_time) VALUES
(2051531076388265995, 0, 2051531076388265986, '星际穿越', 'https://example.com/interstellar.jpg', ',科幻,经典,诺兰,', '非常震撼的科幻电影', 95, '{"year":2014,"director":"克里斯托弗·诺兰"}', '2026-05-01 14:00:00', '2026-05-01 17:00:00', 100, 1, 0, 2, '2026-05-01 14:00:00', '2026-05-01 17:00:00'),
(2051531076388265996, 0, 2051531076388265986, '盗梦空间', 'https://example.com/inception.jpg', ',科幻,悬疑,诺兰,', '梦境与现实的完美交织', 92, '{"year":2010,"director":"克里斯托弗·诺兰"}', '2026-05-02 20:00:00', '2026-05-02 22:30:00', 100, 2, 0, 2, '2026-05-02 20:00:00', '2026-05-02 22:30:00'),
(2051531076388265997, 0, 2051531076388265987, '三体', 'https://example.com/threebody.jpg', ',科幻,刘慈欣,获奖作品,', '中国科幻的巅峰之作', 98, '{"year":2008,"author":"刘慈欣"}', '2026-05-03 09:00:00', NULL, 60, 1, 1, 1, '2026-05-03 09:00:00', '2026-05-04 15:00:00'),
(2051531076388265998, 0, 2051531076388265988, '进击的巨人', 'https://example.com/aot.jpg', ',热血,奇幻,谏山创,', '剧情跌宕起伏，结局争议较大', 88, '{"year":2009,"author":"谏山创"}', '2026-04-15 10:00:00', '2026-04-20 18:00:00', 100, 1, 0, 2, '2026-04-15 10:00:00', '2026-04-20 18:00:00'),
(2051531076388265999, 0, 2051531076388265989, 'Bohemian Rhapsody', 'https://example.com/queen.jpg', ',摇滚,经典,Queen,', '永恒的经典之作', 100, '{"year":1975,"artist":"Queen"}', NULL, NULL, NULL, 1, 0, 0, '2026-05-04 09:00:00', '2026-05-04 09:00:00'),
(2051531076388266000, 0, 2051531076388265989, '夜曲', 'https://example.com/jay.jpg', ',流行,周杰伦,华语,', '周杰伦的经典作品', 95, '{"year":2005,"artist":"周杰伦"}', NULL, NULL, NULL, 2, 0, 0, '2026-05-04 10:00:00', '2026-05-04 10:00:00'),
(2051531076388266001, 0, 2051531076388265990, '塞尔达传说：旷野之息', 'https://example.com/zelda.jpg', ',开放世界,任天堂,冒险,', '自由探索的极致体验', 97, '{"year":2017,"developer":"任天堂"}', '2026-03-10 14:00:00', '2026-04-15 22:00:00', 100, 1, 2, 2, '2026-03-10 14:00:00', '2026-04-15 22:00:00'),
(2051531076388266002, 0, 2051531076388265990, '艾尔登法环', 'https://example.com/eldenring.jpg', ',魂系,FromSoftware,RPG,', '难度高但非常有成就感', 93, '{"year":2022,"developer":"FromSoftware"}', '2026-05-01 10:00:00', NULL, 35, 2, 2, 1, '2026-05-01 10:00:00', '2026-05-05 08:00:00'),
(2051531076388266003, 0, 2051531076388265991, '星尘魔法师 - 艾莉娅', 'https://example.com/oc_alia.jpg', ',魔法,原创角色,奇幻,', '能够操控星尘力量的年轻魔法师', NULL, '{"age":19,"ability":"星尘魔法","personality":"温柔善良"}', '2026-04-01 10:00:00', '2026-04-10 18:00:00', 100, 1, 2, 2, '2026-04-01 10:00:00', '2026-04-10 18:00:00'),
(2051531076388266004, 0, 2051531076388265991, '机械战士 - K-7', 'https://example.com/oc_k7.jpg', ',科幻,机甲,原创角色,', '拥有自我意识的战斗机器人', NULL, '{"model":"K-7","function":"战斗支援","ai_level":"高级"}', '2026-05-02 14:00:00', NULL, 45, 2, 2, 1, '2026-05-02 14:00:00', '2026-05-05 11:00:00');
