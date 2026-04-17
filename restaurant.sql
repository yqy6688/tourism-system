-- 餐饮信息表
CREATE TABLE IF NOT EXISTS `restaurant` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '餐饮ID',
  `name` varchar(100) NOT NULL COMMENT '餐饮名称',
  `type` varchar(50) NOT NULL COMMENT '餐饮类型（中餐/西餐/快餐/火锅/烧烤等）',
  `address` varchar(200) NOT NULL COMMENT '地址',
  `scenic_id` int DEFAULT NULL COMMENT '关联景点ID',
  `description` text COMMENT '餐饮描述',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `price_range` varchar(50) DEFAULT NULL COMMENT '价格区间',
  `average_rating` decimal(2,1) DEFAULT '0.0' COMMENT '平均评分',
  `image_url` varchar(200) DEFAULT NULL COMMENT '主图URL',
  `image_list` text COMMENT '图片列表，JSON格式存储多张图片URL',
  `features` varchar(500) DEFAULT NULL COMMENT '特色菜品',
  `distance` varchar(50) DEFAULT NULL COMMENT '距景点距离',
  `status` varchar(20) NOT NULL DEFAULT 'OPEN' COMMENT '状态：OPEN-营业中, CLOSED-休息中, OFFLINE-已下架',
  `collect_count` int DEFAULT '0' COMMENT '收藏数量',
  `business_hours` varchar(100) DEFAULT NULL COMMENT '营业时间',
  `capacity` int DEFAULT '0' COMMENT '容纳人数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_scenic_id` (`scenic_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_rating` (`average_rating`),
  CONSTRAINT `fk_restaurant_scenic` FOREIGN KEY (`scenic_id`) REFERENCES `scenic_spot` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='餐饮信息表';

-- 餐饮收藏表
CREATE TABLE IF NOT EXISTS `restaurant_collection` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `restaurant_id` int NOT NULL COMMENT '餐饮ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_restaurant` (`user_id`,`restaurant_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_restaurant_id` (`restaurant_id`),
  CONSTRAINT `fk_restaurant_collection_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_restaurant_collection_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='餐饮收藏表';

-- 餐饮评价表
CREATE TABLE IF NOT EXISTS `restaurant_review` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `restaurant_id` int NOT NULL COMMENT '餐饮ID',
  `content` text COMMENT '评价内容',
  `rating` decimal(2,1) NOT NULL COMMENT '评分（1-5分）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_restaurant_id` (`restaurant_id`),
  CONSTRAINT `fk_restaurant_review_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_restaurant_review_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='餐饮评价表';

-- 餐饮预订表
CREATE TABLE IF NOT EXISTS `restaurant_order` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单号',
  `user_id` int NOT NULL COMMENT '用户ID',
  `restaurant_id` int NOT NULL COMMENT '餐饮ID',
  `reservation_date` datetime NOT NULL COMMENT '预订日期',
  `guest_count` int NOT NULL DEFAULT '1' COMMENT '用餐人数',
  `contact_name` varchar(50) NOT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系人电话',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额（预订金）',
  `payment_method` varchar(20) DEFAULT NULL COMMENT '支付方式',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '订单状态：PENDING-待支付, PAID-已支付, CANCELLED-已取消, COMPLETED-已完成',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_restaurant_id` (`restaurant_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_restaurant_order_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_restaurant_order_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='餐饮预订表';

-- 插入测试数据
INSERT INTO `restaurant` (`name`, `type`, `address`, `scenic_id`, `description`, `contact_phone`, `price_range`, `average_rating`, `image_url`, `features`, `distance`, `status`, `collect_count`, `business_hours`, `capacity`) VALUES
('故宫御膳房', '中餐', '北京市东城区故宫博物院东侧', 1, '位于故宫博物院内的传统宫廷菜餐厅，提供正宗的满汉全席和宫廷御膳，环境优雅，服务周到。', '010-65128888', '200-800', 4.8, '/img/restaurant/gugong.jpg', '满汉全席,宫廷御膳,传统点心', '100米', 'OPEN', 1250, '10:00-22:00', 200),
('长城农家乐', '农家菜', '北京市怀柔区慕田峪长城脚下', 2, '长城脚下的农家乐餐厅，提供地道的北方农家菜，食材新鲜，味道纯正。', '010-61618888', '80-200', 4.5, '/img/restaurant/changcheng.jpg', '农家菜,山野菜,烧烤', '500米', 'OPEN', 890, '08:00-20:00', 150),
('西湖楼外楼', '中餐', '浙江省杭州市西湖区孤山路30号', 3, '百年老字号餐厅，以杭帮菜闻名，西湖醋鱼、东坡肉等经典菜品深受游客喜爱。', '0571-87968888', '150-500', 4.7, '/img/restaurant/xihu.jpg', '杭帮菜,西湖醋鱼,东坡肉', '200米', 'OPEN', 1560, '10:30-21:30', 180),
('黄山迎客松餐厅', '徽菜', '安徽省黄山市黄山区汤口镇', 4, '黄山脚下的徽菜餐厅，提供正宗的徽州特色菜，环境优美，服务热情。', '0559-5586888', '120-400', 4.6, '/img/restaurant/huangshan.jpg', '徽菜,毛豆腐,臭鳜鱼', '1公里', 'OPEN', 780, '09:00-21:00', 120),
('漓江渔家', '海鲜', '广西壮族自治区桂林市阳朔县西街', 5, '漓江边的海鲜餐厅，以漓江鲜鱼和当地特色菜为主，环境优雅，可欣赏漓江美景。', '0773-8821888', '100-300', 4.4, '/img/restaurant/lijiang.jpg', '漓江鲜鱼,啤酒鱼,田螺酿', '300米', 'OPEN', 650, '11:00-22:00', 100),
('北京烤鸭店', '中餐', '北京市东城区王府井大街', 1, '著名的北京烤鸭专门店，提供正宗的北京烤鸭和其他京味小吃。', '010-65228888', '150-400', 4.9, '/img/restaurant/kaoya.jpg', '北京烤鸭,京味小吃', '2公里', 'OPEN', 2100, '10:00-22:30', 250),
('西湖茶餐厅', '茶餐厅', '浙江省杭州市西湖区杨公堤', 3, '西湖边的茶餐厅，提供简餐、茶点和咖啡，环境舒适，适合休闲小憩。', '0571-87998888', '50-150', 4.3, '/img/restaurant/chacanting.jpg', '简餐,茶点,咖啡', '100米', 'OPEN', 420, '08:00-23:00', 80),
('黄山土菜馆', '农家菜', '安徽省黄山市黄山区汤口镇老街', 4, '黄山老街上的土菜馆，提供地道的黄山土菜，价格实惠，味道正宗。', '0559-5581234', '60-180', 4.2, '/img/restaurant/tucaiguan.jpg', '黄山土菜,山野菜', '800米', 'OPEN', 320, '09:30-20:30', 60),
('阳朔西餐厅', '西餐', '广西壮族自治区桂林市阳朔县西街', 5, '阳朔西街上的西餐厅，提供西式简餐和咖啡，环境优雅，适合情侣约会。', '0773-8822888', '80-200', 4.5, '/img/restaurant/xican.jpg', '牛排,意面,咖啡', '50米', 'OPEN', 580, '10:00-23:00', 90),
('老北京炸酱面', '快餐', '北京市东城区前门大街', 1, '传统的老北京炸酱面馆，提供正宗的炸酱面和其他北京小吃。', '010-65118888', '30-80', 4.1, '/img/restaurant/zhajiangmian.jpg', '炸酱面,北京小吃', '1.5公里', 'OPEN', 950, '07:00-21:00', 40);

-- 插入评价数据
INSERT INTO `restaurant_review` (`user_id`, `restaurant_id`, `content`, `rating`) VALUES
(2, 1, '环境优雅，服务周到，宫廷菜很有特色，值得一试！', 5.0),
(3, 1, '菜品精致，味道不错，就是价格稍高。', 4.5),
(4, 2, '农家菜很地道，食材新鲜，价格实惠。', 4.8),
(2, 3, '百年老字号果然名不虚传，西湖醋鱼特别好吃！', 5.0),
(3, 4, '徽菜很有特色，毛豆腐和臭鳜鱼都很正宗。', 4.6),
(4, 5, '漓江边的环境很好，鱼很新鲜，推荐！', 4.4),
(2, 6, '北京烤鸭非常正宗，皮脆肉嫩，强烈推荐！', 5.0),
(3, 7, '茶餐厅环境不错，适合休闲，价格适中。', 4.3),
(4, 8, '土菜馆价格实惠，味道不错，性价比高。', 4.2),
(2, 9, '西餐厅环境优雅，牛排做得不错。', 4.5),
(3, 10, '炸酱面很正宗，价格便宜，适合快速用餐。', 4.1);