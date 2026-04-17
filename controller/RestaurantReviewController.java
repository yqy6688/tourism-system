package org.example.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.RestaurantReview;
import org.example.springboot.service.RestaurantReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 餐饮评价控制器
 */
@Tag(name = "餐饮评价接口")
@RestController
@RequestMapping("/restaurant/review")
public class RestaurantReviewController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantReviewController.class);
    
    @Resource
    private RestaurantReviewService restaurantReviewService;
    
    @Operation(summary = "获取餐饮评价列表")
    @GetMapping("/page")
    public Result<?> getReviewList(
            @RequestParam Integer restaurantId,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            LOGGER.info("获取餐饮{}的评价列表，页码：{}，大小：{}", restaurantId, currentPage, size);
            List<RestaurantReview> reviews = restaurantReviewService.getReviewList(restaurantId, currentPage, size);
            return Result.success(reviews);
        } catch (Exception e) {
            LOGGER.error("获取评价列表失败", e);
            return Result.error("获取评价列表失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取餐饮评价总数")
    @GetMapping("/count")
    public Result<?> getReviewCount(@RequestParam Integer restaurantId) {
        try {
            LOGGER.info("获取餐饮{}的评价总数", restaurantId);
            Integer count = restaurantReviewService.getReviewCount(restaurantId);
            return Result.success(count);
        } catch (Exception e) {
            LOGGER.error("获取评价总数失败", e);
            return Result.error("获取评价总数失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取餐饮评分统计")
    @GetMapping("/stats")
    public Result<?> getRatingStats(@RequestParam Integer restaurantId) {
        try {
            LOGGER.info("获取餐饮{}的评分统计", restaurantId);
            Map<Integer, Integer> stats = restaurantReviewService.getRatingStats(restaurantId);
            return Result.success(stats);
        } catch (Exception e) {
            LOGGER.error("获取评分统计失败", e);
            return Result.error("获取评分统计失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "提交评价")
    @PostMapping
    public Result<?> submitReview(@RequestBody RestaurantReview review) {
        try {
            LOGGER.info("用户{}提交对餐饮{}的评价，评分：{}", review.getUserId(), review.getRestaurantId(), review.getRating());
            boolean result = restaurantReviewService.submitReview(review);
            if (result) {
                return Result.success();
            } else {
                return Result.error("提交评价失败");
            }
        } catch (Exception e) {
            LOGGER.error("提交评价失败", e);
            return Result.error("提交评价失败：" + e.getMessage());
        }
    }
}