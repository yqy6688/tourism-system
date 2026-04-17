package org.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.springboot.entity.RestaurantOrder;
import org.example.springboot.mapper.RestaurantOrderMapper;
import org.example.springboot.service.RestaurantOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 餐饮订单服务实现类
 */
@Service
public class RestaurantOrderServiceImpl extends ServiceImpl<RestaurantOrderMapper, RestaurantOrder> implements RestaurantOrderService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantOrderServiceImpl.class);
    
    @Resource
    private RestaurantOrderMapper restaurantOrderMapper;
    
    @Override
    @Transactional
    public RestaurantOrder createOrder(RestaurantOrder order) {
        try {
            // 生成订单号
            String orderNo = "R" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
            order.setOrderNo(orderNo);
            order.setStatus("PENDING");
            order.setCreateTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            
            boolean result = this.save(order);
            if (result) {
                return order;
            }
            throw new RuntimeException("创建订单失败");
        } catch (Exception e) {
            LOGGER.error("创建餐饮订单失败", e);
            throw new RuntimeException("创建餐饮订单失败", e);
        }
    }
    
    @Override
    @Transactional
    public boolean payOrder(Integer orderId, String paymentMethod) {
        try {
            RestaurantOrder order = this.getById(orderId);
            if (order == null) {
                throw new RuntimeException("订单不存在");
            }
            
            if (!"PENDING".equals(order.getStatus())) {
                throw new RuntimeException("订单状态异常，无法支付");
            }
            
            order.setStatus("PAID");
            order.setPaymentMethod(paymentMethod);
            order.setPayTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            
            return this.updateById(order);
        } catch (Exception e) {
            LOGGER.error("支付餐饮订单失败", e);
            throw new RuntimeException("支付餐饮订单失败", e);
        }
    }
    
    @Override
    @Transactional
    public boolean cancelOrder(Integer orderId) {
        try {
            RestaurantOrder order = this.getById(orderId);
            if (order == null) {
                throw new RuntimeException("订单不存在");
            }
            
            if (!("PENDING".equals(order.getStatus()) || "PAID".equals(order.getStatus()))) {
                throw new RuntimeException("订单状态异常，无法取消");
            }
            
            order.setStatus("CANCELLED");
            order.setCancelTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            
            return this.updateById(order);
        } catch (Exception e) {
            LOGGER.error("取消餐饮订单失败", e);
            throw new RuntimeException("取消餐饮订单失败", e);
        }
    }
    
    @Override
    public List<RestaurantOrder> getUserOrders(Integer userId) {
        try {
            return restaurantOrderMapper.selectUserOrders(userId);
        } catch (Exception e) {
            LOGGER.error("获取用户餐饮订单列表失败", e);
            throw new RuntimeException("获取用户餐饮订单列表失败", e);
        }
    }
    
    @Override
    public RestaurantOrder getOrderById(Integer orderId) {
        try {
            return this.getById(orderId);
        } catch (Exception e) {
            LOGGER.error("获取餐饮订单详情失败", e);
            throw new RuntimeException("获取餐饮订单详情失败", e);
        }
    }
    /**
     * 根据订单号获取取消的订单
     */

    @Override
    public boolean getCancelOrdersByOrderNo(String orderNo) {
        LOGGER.info("getCancelOrdersByOrderNo {}", orderNo);
        LambdaQueryWrapper<RestaurantOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RestaurantOrder::getOrderNo, orderNo);
        RestaurantOrder restaurantOrder = restaurantOrderMapper.selectOne(wrapper);
        if (restaurantOrder == null) {
            return false;
        }
        restaurantOrder.setStatus("CANCELLED");
        int i = restaurantOrderMapper.updateById(restaurantOrder);
        if (i > 0){
            return true;
        }
        return false;
    }
}