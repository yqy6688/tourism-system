package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.dto.response.UnifiedOrderDTO;
import org.example.springboot.entity.User;
import org.example.springboot.service.UnifiedOrderService;
import org.example.springboot.util.JwtTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一订单管理控制器
 * 支持景点门票、餐饮、酒店三种订单的统一管理
 */
@Tag(name = "统一订单管理接口")
@RestController
@RequestMapping("/order/unified")
public class UnifiedOrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnifiedOrderController.class);

    @Resource
    private UnifiedOrderService unifiedOrderService;

    @Operation(summary = "分页查询所有类型订单（管理员专用）")
    @GetMapping("/page")
    public Result<?> getAllOrdersByPage(
            @RequestParam(defaultValue = "") String orderNo,
            @RequestParam(defaultValue = "") String contactName,
            @RequestParam(defaultValue = "") String contactPhone,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String orderType,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {



        LOGGER.info("分页查询所有订单: orderNo={}, contactName={}, contactPhone={}, status={}, orderType={}, currentPage={}, size={}",
                orderNo, contactName, contactPhone, status, orderType, currentPage, size);

        Page<UnifiedOrderDTO> page = unifiedOrderService.getAllOrdersByPage(
                orderNo, contactName, contactPhone, status, orderType, currentPage, size);
        return Result.success(page);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{orderType}/{id}")
    public Result<?> getOrderDetail(
            @PathVariable String orderType,
            @PathVariable Long id) {



        LOGGER.info("获取订单详情: orderType={}, id={}", orderType, id);

        UnifiedOrderDTO order = unifiedOrderService.getOrderDetail(orderType, id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    @Operation(summary = "退款订单（管理员专用）")
    @PostMapping("/{orderType}/{id}/refund")
    public Result<?> refundOrder(
            @PathVariable String orderType,
            @PathVariable Long id) {


        LOGGER.info("退款订单: orderType={}, id={}", orderType, id);

        boolean result = unifiedOrderService.refundOrder(orderType, id);
        if (result) {
            return Result.success("退款成功");
        } else {
            return Result.error("退款失败");
        }
    }

    @Operation(summary = "完成订单（管理员专用）")
    @PostMapping("/{orderType}/{id}/complete")
    public Result<?> completeOrder(
            @PathVariable String orderType,
            @PathVariable Long id) {



        LOGGER.info("完成订单: orderType={}, id={}", orderType, id);

        boolean result = unifiedOrderService.completeOrder(orderType, id);
        if (result) {
            return Result.success("订单已完成");
        } else {
            return Result.error("操作失败");
        }
    }

    @Operation(summary = "删除订单（管理员专用）")
    @DeleteMapping("/{orderType}/{id}")
    public Result<?> deleteOrder(
            @PathVariable String orderType,
            @PathVariable Long id) {

        // 验证当前用户是否是管理员
        User currentUser = JwtTokenUtils.getCurrentUser();


        LOGGER.info("删除订单: orderType={}, id={}", orderType, id);

        boolean result = unifiedOrderService.deleteOrder(orderType, id);
        if (result) {
            return Result.success("订单删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    @Operation(summary = "获取订单统计信息")
    @GetMapping("/stats")
    public Result<?> getOrderStats() {

        LOGGER.info("获取订单统计信息");

        Map<String, Object> stats = unifiedOrderService.getOrderStats();
        return Result.success(stats);
    }
}
