package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.springboot.DTO.response.AccommodationOrderDTO;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Accommodation;
import org.example.springboot.entity.AccommodationOrder;
import org.example.springboot.service.AccommodationOrderService;
import org.example.springboot.service.AccommodationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 酒店订单控制器
 */
@Tag(name = "酒店订单接口")
@RestController
@RequestMapping("/accommodation/order")
public class AccommodationOrderController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AccommodationOrderController.class);
    
    @Resource
    private AccommodationOrderService accommodationOrderService;
    @Resource
    private AccommodationService accommodationService;
    
    @Operation(summary = "创建酒店订单")
    @PostMapping
    public Result<?> createOrder(@RequestBody AccommodationOrder order) {
        try {
            LOGGER.info("创建酒店订单，用户ID：{}", order.getUserId());
            AccommodationOrder createdOrder = accommodationOrderService.createOrder(order);
            return Result.success(createdOrder);
        } catch (Exception e) {
            LOGGER.error("创建酒店订单失败", e);
            return Result.error("创建订单失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "支付酒店订单")
    @PostMapping("/{id}/pay")
    public Result<?> payOrder(@PathVariable Integer id, @RequestParam String paymentMethod) {
        try {
            LOGGER.info("支付酒店订单，订单ID：{}，支付方式：{}", id, paymentMethod);
            boolean result = accommodationOrderService.payOrder(id, paymentMethod);
            if (result) {
                return Result.success();
            } else {
                return Result.error("支付失败");
            }
        } catch (Exception e) {
            LOGGER.error("支付酒店订单失败", e);
            return Result.error("支付失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "取消酒店订单")
    @PostMapping("/{id}/cancel")
    public Result<?> cancelOrder(@PathVariable Integer id) {
        try {
            LOGGER.info("取消酒店订单，订单ID：{}", id);
            boolean result = accommodationOrderService.cancelOrder(id);
            if (result) {
                return Result.success();
            } else {
                return Result.error("取消失败");
            }
        } catch (Exception e) {
            LOGGER.error("取消酒店订单失败", e);
            return Result.error("取消失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取用户酒店订单列表")
    @GetMapping("/user/{userId}")
    public Result<?> getUserOrders(@PathVariable Integer userId) {
        try {
            LOGGER.info("获取用户酒店订单列表，用户ID：{}", userId);
            List<AccommodationOrder> orders = accommodationOrderService.getUserOrders(userId);
            return Result.success(orders);
        } catch (Exception e) {
            LOGGER.error("获取用户酒店订单列表失败", e);
            return Result.error("获取订单失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<?> getOrderById(@PathVariable Integer id) {
        try {
            LOGGER.info("获取酒店订单详情，订单ID：{}", id);
            AccommodationOrder order = accommodationOrderService.getOrderById(id);
            if (order == null) {
                return Result.error("订单不存在");
            }
            AccommodationOrderDTO accommodationOrderDTO = new AccommodationOrderDTO();
            LOGGER.info("获取酒店订单详情成功，订单ID：{}", id);
            Accommodation accommodation = accommodationService.getAccommodationById(order.getAccommodationId());
            BeanUtils.copyProperties(order, accommodationOrderDTO);
            accommodationOrderDTO.setAccommodationName(accommodation.getName());
            return Result.success(accommodationOrderDTO);
        } catch (Exception e) {
            LOGGER.error("获取酒店订单详情失败", e);
            return Result.error("获取订单失败：" + e.getMessage());
        }
    }
}
