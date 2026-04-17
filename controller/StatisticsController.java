package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.mapper.*;
import org.example.springboot.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name="统计管理接口")
@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsController.class);
    
    @Resource
    private StatisticsService statisticsService;

    @Operation(summary = "获取基础统计数据")
    @GetMapping("/basic")
    public Result<?> getBasicStatistics() {
        try {
            Map<String, Object> statistics = statisticsService.getBasicStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            LOGGER.error("获取基础统计数据失败", e);
            return Result.error("获取基础统计数据失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取用户性别统计")
    @GetMapping("/user/sex")
    public Result<?> getUserSexStatistics() {
        try {
            Map<String, Object> statistics = statisticsService.getUserSexStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            LOGGER.error("获取用户性别统计失败", e);
            return Result.error("获取用户性别统计失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取景点分类统计")
    @GetMapping("/scenic/category")
    public Result<?> getScenicCategoryStatistics() {
        try {
            Map<String, Object> statistics = statisticsService.getScenicCategoryStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            LOGGER.error("获取景点分类统计失败", e);
            return Result.error("获取景点分类统计失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取订单统计")
    @GetMapping("/order")
    public Result<?> getOrderStatistics() {
        try {
            Map<String, Object> statistics = statisticsService.getOrderStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            LOGGER.error("获取订单统计失败", e);
            return Result.error("获取订单统计失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取景点票价统计")
    @GetMapping("/scenic/price")
    public Result<?> getScenicPriceStatistics() {
        try {
            Map<String, Object> statistics = statisticsService.getScenicPriceStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            LOGGER.error("获取景点票价统计失败", e);
            return Result.error("获取景点票价统计失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取攻略数量统计")
    @GetMapping("/guide/count")
    public Result<?> getGuideCountStatistics() {
        try {
            Map<String, Object> statistics = statisticsService.getGuideCountStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            LOGGER.error("获取攻略数量统计失败", e);
            return Result.error("获取攻略数量统计失败：" + e.getMessage());
        }
    }
}
