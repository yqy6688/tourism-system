package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Restaurant;
import org.example.springboot.entity.RestaurantOrder;
import org.example.springboot.service.RestaurantOrderService;
import org.example.springboot.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 餐饮订单控制器
 */
@Tag(name = "餐饮订单接口")
@RestController
@RequestMapping("/restaurant/order")
public class RestaurantOrderController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantOrderController.class);
    
    @Resource
    private RestaurantOrderService restaurantOrderService;
    @Resource
    private RestaurantService restaurantService;
    
    @Operation(summary = "创建餐饮订单")
    @PostMapping
    public Result<?> createOrder(@RequestBody RestaurantOrder order) {
        try {
            LOGGER.info("用户{}创建餐饮订单，餐饮ID：{}", order.getUserId(), order.getRestaurantId());
            RestaurantOrder createdOrder = restaurantOrderService.createOrder(order);
            return Result.success(createdOrder);
        } catch (Exception e) {
            LOGGER.error("创建餐饮订单失败", e);
            return Result.error("创建订单失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "支付餐饮订单")
    @PutMapping("/pay")
    public Result<?> payOrder(@RequestBody Map<String, Object> params) {
        try {
            Integer orderId = (Integer) params.get("orderId");
            String paymentMethod = (String) params.get("paymentMethod");
            
            LOGGER.info("支付餐饮订单{}，支付方式：{}", orderId, paymentMethod);
            boolean result = restaurantOrderService.payOrder(orderId, paymentMethod);
            if (result) {
                return Result.success();
            } else {
                return Result.error("支付失败");
            }
        } catch (Exception e) {
            LOGGER.error("支付餐饮订单失败", e);
            return Result.error("支付失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "取消餐饮订单")
    @PutMapping("/cancel")
    public Result<?> cancelOrder(@RequestBody Map<String, Object> params) {
        try {
            Integer orderId = (Integer) params.get("orderId");
            
            LOGGER.info("取消餐饮订单{}", orderId);
            boolean result = restaurantOrderService.cancelOrder(orderId);
            if (result) {
                return Result.success();
            } else {
                return Result.error("取消订单失败");
            }
        } catch (Exception e) {
            LOGGER.error("取消餐饮订单失败", e);
            return Result.error("取消订单失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取用户餐饮订单列表")
    @GetMapping("/user/{userId}")
    public Result<?> getUserOrders(@PathVariable Integer userId) {
        try {
            LOGGER.info("获取用户{}的餐饮订单列表", userId);
            List<RestaurantOrder> orders = restaurantOrderService.getUserOrders(userId);
            return Result.success(orders);
        } catch (Exception e) {
            LOGGER.error("获取用户餐饮订单列表失败", e);
            return Result.error("获取订单列表失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取餐饮订单详情")
    @GetMapping("/{orderId}")
    public Result<?> getOrderDetail(@PathVariable Integer orderId) {
        try {
            LOGGER.info("获取餐饮订单{}详情", orderId);
            RestaurantOrder order = restaurantOrderService.getOrderById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            Restaurant restaurant = restaurantService.getRestaurantById(order.getRestaurantId());
            if (restaurant == null){
                return Result.error("餐厅信息不存在");
            }
            order.setRestaurantName(restaurant.getName());
            return Result.success(order);
        } catch (Exception e) {
            LOGGER.error("获取餐饮订单详情失败", e);
            return Result.error("获取订单详情失败：" + e.getMessage());
        }
    }
    @Operation(summary = "取消预订订单列表")
    @GetMapping("/cancel/orderNo/{orderNo}")
    public Result<?> getCancelOrders( String orderNo) {
        try {
            LOGGER.info("订单号{}的取消预订订单列表", orderNo);
            boolean order = restaurantOrderService.getCancelOrdersByOrderNo(orderNo);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error("取消订单列表失败：" + e.getMessage());
        }
    }
}