package org.example.springboot.service;

import org.example.springboot.entity.AccommodationOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 酒店订单服务接口
 */
public interface AccommodationOrderService extends IService<AccommodationOrder> {
    
    /**
     * 创建酒店订单
     * @param order 订单信息
     * @return 创建的订单
     */
    AccommodationOrder createOrder(AccommodationOrder order);
    
    /**
     * 支付订单
     * @param orderId 订单ID
     * @param paymentMethod 支付方式
     * @return 是否支付成功
     */
    boolean payOrder(Integer orderId, String paymentMethod);
    
    /**
     * 取消订单
     * @param orderId 订单ID
     * @return 是否取消成功
     */
    boolean cancelOrder(Integer orderId);
    
    /**
     * 获取用户的酒店订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    java.util.List<AccommodationOrder> getUserOrders(Integer userId);
    
    /**
     * 获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    AccommodationOrder getOrderById(Integer orderId);
}
