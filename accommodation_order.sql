-- 酒店订单表创建脚本
-- 请在MySQL数据库中执行此脚本

CREATE TABLE IF NOT EXISTS `accommodation_order` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单号',
  `user_id` int NOT NULL COMMENT '用户ID',
  `accommodation_id` int NOT NULL COMMENT '酒店ID',
  `room_id` int DEFAULT NULL COMMENT '房型ID',
  `check_in_date` datetime NOT NULL COMMENT '入住日期',
  `check_out_date` datetime NOT NULL COMMENT '退房日期',
  `guest_count` int NOT NULL DEFAULT '1' COMMENT '入住人数',
  `contact_name` varchar(50) NOT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系人电话',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `days` int NOT NULL COMMENT '入住天数',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
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
  KEY `idx_accommodation_id` (`accommodation_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='酒店订单表';

-- 添加外键约束（如果相关表存在）
-- ALTER TABLE `accommodation_order` ADD CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;
-- ALTER TABLE `accommodation_order` ADD CONSTRAINT `fk_order_accommodation` FOREIGN KEY (`accommodation_id`) REFERENCES `accommodation` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

-- 插入测试数据（可选）
INSERT INTO `accommodation_order` (
  `order_no`, `user_id`, `accommodation_id`, `room_id`, `check_in_date`, `check_out_date`, 
  `guest_count`, `contact_name`, `contact_phone`, `days`, `total_amount`, `status`
) VALUES 
('ACC202503161508001', 1, 1, 1, '2025-03-20 14:00:00', '2025-03-22 12:00:00', 2, '张三', '13800138000', 2, 576.00, 'PAID'),
('ACC202503161508002', 2, 2, 2, '2025-03-25 14:00:00', '2025-03-27 12:00:00', 2, '李四', '13900139000', 2, 656.00, 'PENDING'),
('ACC202503161508003', 3, 3, 3, '2025-04-01 14:00:00', '2025-04-03 12:00:00', 3, '王五', '13700137000', 2, 976.00, 'CANCELLED');

-- 查看表结构
DESCRIBE `accommodation_order`;

-- 查看测试数据
SELECT * FROM `accommodation_order`;