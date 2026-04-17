package org.example.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.springboot.entity.AccommodationOrder;
import org.example.springboot.mapper.AccommodationOrderMapper;
import org.example.springboot.service.AccommodationOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 酒店订单服务实现
 */
@Service
public class AccommodationOrderServiceImpl extends ServiceImpl<AccommodationOrderMapper, AccommodationOrder> implements AccommodationOrderService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AccommodationOrderServiceImpl.class);
    
    @Override
    public AccommodationOrder createOrder(AccommodationOrder order) {
        try {
            // 生成订单号
            String orderNo = "ACC" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            order.setOrderNo(orderNo);
            order.setStatus("PENDING");
            order.setCreateTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            
            save(order);
            LOGGER.info("创建酒店订单成功，订单号：{}", orderNo);
            return order;
        } catch (Exception e) {
            LOGGER.error("创建酒店订单失败", e);
            throw new RuntimeException("创建订单失败", e);
        }
    }
    
    @Override
    public boolean payOrder(Integer orderId, String paymentMethod) {
        try {
            AccommodationOrder order = getById(orderId);
            if (order == null) {
                throw new RuntimeException("订单不存在");
            }
            if (!"PENDING".equals(order.getStatus())) {
                throw new RuntimeException("订单状态错误");
            }
            
            order.setStatus("PAID");
            order.setPaymentMethod(paymentMethod);
            order.setPayTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            
            return updateById(order);
        } catch (Exception e) {
            LOGGER.error("支付订单失败", e);
            throw new RuntimeException("支付失败", e);
        }
    }
    
    @Override
    public boolean cancelOrder(Integer orderId) {
        try {
            AccommodationOrder order = getById(orderId);
            if (order == null) {
                throw new RuntimeException("订单不存在");
            }
            if (!("PENDING".equals(order.getStatus()) || "PAID".equals(order.getStatus()))) {
                throw new RuntimeException("订单状态错误");
            }
            
            order.setStatus("CANCELLED");
            order.setCancelTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            
            return updateById(order);
        } catch (Exception e) {
            LOGGER.error("取消订单失败", e);
            throw new RuntimeException("取消失败", e);
        }
    }
    
    @Override
    public java.util.List<AccommodationOrder> getUserOrders(Integer userId) {
        try {
            return lambdaQuery().eq(AccommodationOrder::getUserId, userId).orderByDesc(AccommodationOrder::getCreateTime).list();
        } catch (Exception e) {
            LOGGER.error("获取用户酒店订单失败", e);
            throw new RuntimeException("获取订单失败", e);
        }
    }
    
    @Override
    public AccommodationOrder getOrderById(Integer orderId) {
        try {
            return getById(orderId);
        } catch (Exception e) {
            LOGGER.error("获取订单详情失败", e);
            throw new RuntimeException("获取订单失败", e);
        }
    }
}
