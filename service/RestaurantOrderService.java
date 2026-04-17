package org.example.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.springboot.entity.RestaurantOrder;

import java.util.List;
import java.util.UUID;

/**
 * 餐饮订单服务接口
 */
public interface RestaurantOrderService extends IService<RestaurantOrder> {
    
    /**
     * 创建餐饮订单
     */
    RestaurantOrder createOrder(RestaurantOrder order);
    
    /**
     * 支付餐饮订单
     */
    boolean payOrder(Integer orderId, String paymentMethod);
    
    /**
     * 取消餐饮订单
     */
    boolean cancelOrder(Integer orderId);
    
    /**
     * 获取用户餐饮订单列表
     */
    List<RestaurantOrder> getUserOrders(Integer userId);
    
    /**
     * 获取订单详情
     */
    RestaurantOrder getOrderById(Integer orderId);
    /**
     * 根据订单号获取取消的订单
     */

    boolean getCancelOrdersByOrderNo(String orderNo);
}