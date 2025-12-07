USE picturemanager;

DROP TABLE IF EXISTS user_tokens;
DROP TABLE IF EXISTS users;

-- 用户表
CREATE TABLE `users` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '用户的唯一标识符',
    `username` VARCHAR(255) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '用户的密码，使用BCrypt加密存储',
    `email` VARCHAR(255) NOT NULL COMMENT '用户的邮箱',
    `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '用户头像的存储路径',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 登录令牌表
CREATE TABLE `user_tokens` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '令牌ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `token` VARCHAR(500) NOT NULL COMMENT '登录令牌',
    `device_info` VARCHAR(255) DEFAULT NULL COMMENT '设备信息',
    `ip_address` VARCHAR(45) DEFAULT NULL COMMENT '登录IP',
    `expiry_time` DATETIME NOT NULL COMMENT '过期时间',
    `is_valid` TINYINT DEFAULT 1 COMMENT '是否有效（1有效，0无效）',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_token` (`token`(255)),
    KEY `idx_expiry` (`expiry_time`),
    CONSTRAINT `fk_token_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户登录令牌表';

INSERT INTO `users` (`username`,`password`,`email`,`avatar_url`)
VALUE ('demo',123456,'demo@demo.com',NULL)