-- ============================================================================
-- 尚医通 新增功能SQL
-- 医院收藏表 + 数据库索引优化
-- ============================================================================

USE `yygh_user`;

-- 用户收藏表
CREATE TABLE IF NOT EXISTS `user_favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `hoscode` varchar(30) NOT NULL COMMENT '医院编号',
  `hosname` varchar(100) DEFAULT NULL COMMENT '医院名称（冗余字段）',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '逻辑删除(1:已删除，0:未删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_hoscode` (`user_id`, `hoscode`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏医院表';

-- ============================================================================
-- 性能优化：为高频查询字段添加索引
-- ============================================================================
USE `yygh_cmn`;
CREATE INDEX IF NOT EXISTS `idx_dict_parent_id` ON `dict` (`parent_id`);
CREATE INDEX IF NOT EXISTS `idx_dict_dict_code` ON `dict` (`dict_code`, `value`);

USE `yygh_order`;
CREATE INDEX IF NOT EXISTS `idx_order_user_id` ON `order_info` (`user_id`);
CREATE INDEX IF NOT EXISTS `idx_order_schedule_id` ON `order_info` (`schedule_id`);
CREATE INDEX IF NOT EXISTS `idx_order_reserve_date` ON `order_info` (`reserve_date`);
CREATE INDEX IF NOT EXISTS `idx_order_hoscode` ON `order_info` (`hoscode`);

USE `yygh_user`;
CREATE UNIQUE INDEX IF NOT EXISTS `idx_user_openid` ON `user_info` (`openid`);
CREATE UNIQUE INDEX IF NOT EXISTS `idx_user_phone` ON `user_info` (`phone`);
CREATE INDEX IF NOT EXISTS `idx_patient_user_id` ON `patient` (`user_id`);

-- ============================================================================
-- 就诊评价表
-- ============================================================================
USE `yygh_user`;
CREATE TABLE IF NOT EXISTS `evaluation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户昵称（脱敏）',
  `hoscode` varchar(30) NOT NULL COMMENT '医院编号',
  `hosname` varchar(100) DEFAULT NULL COMMENT '医院名称',
  `depname` varchar(50) DEFAULT NULL COMMENT '科室名称',
  `title` varchar(50) DEFAULT NULL COMMENT '医生职称',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单id',
  `rating` tinyint(1) NOT NULL COMMENT '评分(1-5星)',
  `content` text COMMENT '评价内容',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '逻辑删除(1:已删除，0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_eval_hoscode` (`hoscode`),
  KEY `idx_eval_user_order` (`user_id`, `order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='就诊评价表';

-- ============================================================================
-- 体检套餐表
-- ============================================================================
USE `yygh_user`;
CREATE TABLE IF NOT EXISTS `checkup_package` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `hoscode` varchar(30) NOT NULL COMMENT '医院编号',
  `hosname` varchar(100) DEFAULT NULL COMMENT '医院名称',
  `name` varchar(100) NOT NULL COMMENT '套餐名称',
  `price` decimal(10,2) NOT NULL COMMENT '套餐价格',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `content` text COMMENT '套餐内容（换行分隔）',
  `suitable` varchar(200) DEFAULT NULL COMMENT '适用人群',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '逻辑删除(1:已删除，0:未删除)',
  PRIMARY KEY (`id`),
  KEY `idx_checkup_hoscode` (`hoscode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='体检套餐表';

-- 体检套餐测试数据
INSERT INTO `checkup_package` (`hoscode`, `hosname`, `name`, `price`, `original_price`, `content`, `suitable`, `sort`) VALUES
('1000_0', '北京协和医院', '基础体检套餐', 588.00, 980.00, '一般检查\n血常规\n尿常规\n肝功能四项\n肾功能三项\n血脂二项\n空腹血糖\n心电图\n胸片\n腹部B超', '适合20-40岁人群基础健康筛查', 1),
('1000_0', '北京协和医院', '精英体检套餐', 1280.00, 1980.00, '包含基础套餐所有项目\n肿瘤标志物筛查\n甲状腺功能\n同型半胱氨酸\n颈动脉B超\n骨密度检测\n中医体质辨识', '适合40岁以上人群深度健康检查', 2),
('1000_0', '北京协和医院', '女性专项套餐', 888.00, 1380.00, '基础体检\n妇科检查\n乳腺彩超\n宫颈TCT\nHPV检测\n盆腔B超\n激素六项', '适合成年女性专项健康检查', 3);