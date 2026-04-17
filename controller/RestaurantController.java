package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Restaurant;
import org.example.springboot.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 餐饮控制器
 */
@Tag(name = "餐饮接口")
@RestController
@RequestMapping("/restaurant")
public class RestaurantController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantController.class);
    
    @Resource
    private RestaurantService restaurantService;
    
    @Operation(summary = "分页查询餐饮列表")
    @GetMapping("/page")
    public Result<?> getRestaurantPage(
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "12") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer scenicId,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String status) {
        try {
            LOGGER.info("查询餐饮列表，页码：{}，大小：{}，名称：{}，类型：{}", currentPage, size, name, type);
            Page<Restaurant> page = restaurantService.getRestaurantPage(currentPage, size, name, type, 
                    scenicId, minPrice, maxPrice, minRating, sortBy, status);
            return Result.success(page);
        } catch (Exception e) {
            LOGGER.error("查询餐饮列表失败", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取餐饮详情")
    @GetMapping("/{id}")
    public Result<?> getRestaurantDetail(@PathVariable Integer id) {
        try {
            LOGGER.info("获取餐饮详情，ID：{}", id);
            Restaurant restaurant = restaurantService.getById(id);
            if (restaurant == null) {
                return Result.error("餐饮信息不存在");
            }
            return Result.success(restaurant);
        } catch (Exception e) {
            LOGGER.error("获取餐饮详情失败", e);
            return Result.error("获取详情失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取所有餐饮类型")
    @GetMapping("/types")
    public Result<?> getAllTypes() {
        try {
            LOGGER.info("获取餐饮类型列表");
            List<String> types = restaurantService.getAllTypes();
            return Result.success(types);
        } catch (Exception e) {
            LOGGER.error("获取餐饮类型失败", e);
            return Result.error("获取类型失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "收藏餐饮")
    @PutMapping("/collect")
    public Result<?> collectRestaurant(@RequestParam Integer userId, @RequestParam Integer restaurantId) {
        try {
            LOGGER.info("用户{}收藏餐饮{}", userId, restaurantId);
            boolean result = restaurantService.collectRestaurant(userId, restaurantId);
            if (result) {
                return Result.success();
            } else {
                return Result.error("收藏失败");
            }
        } catch (Exception e) {
            LOGGER.error("收藏餐饮失败", e);
            return Result.error("收藏失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "取消收藏餐饮")
    @DeleteMapping("/collect")
    public Result<?> cancelCollectRestaurant(@RequestParam(required = true) Integer userId, @RequestParam(required = true) Integer restaurantId) {
        try {
            LOGGER.info("用户{}取消收藏餐饮{}", userId, restaurantId);
            boolean result = restaurantService.cancelCollectRestaurant(userId, restaurantId);
            if (result) {
                return Result.success();
            } else {
                return Result.error("取消收藏失败");
            }
        } catch (Exception e) {
            LOGGER.error("取消收藏餐饮失败", e);
            return Result.error("取消收藏失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "检查收藏状态")
    @GetMapping("/collect/status")
    public Result<?> checkCollectStatus(@RequestParam Integer userId, @RequestParam Integer restaurantId) {
        try {
            LOGGER.info("检查用户{}对餐饮{}的收藏状态", userId, restaurantId);
            boolean isCollected = restaurantService.isCollected(userId, restaurantId);
            return Result.success(isCollected);
        } catch (Exception e) {
            LOGGER.error("检查收藏状态失败", e);
            return Result.error("检查收藏状态失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取用户收藏列表")
    @GetMapping("/collect/user/{userId}")
    public Result<?> getUserCollections(@PathVariable Integer userId) {
        try {
            LOGGER.info("获取用户{}的餐饮收藏列表", userId);
            List<Restaurant> collections = restaurantService.getUserCollections(userId);
            return Result.success(collections);
        } catch (Exception e) {
            LOGGER.error("获取用户收藏列表失败", e);
            return Result.error("获取收藏列表失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "新增餐饮")
    @PostMapping
    public Result<?> addRestaurant(@RequestBody Restaurant restaurant) {
        try {
            LOGGER.info("新增餐饮：{}", restaurant.getName());
            boolean result = restaurantService.save(restaurant);
            if (result) {
                return Result.success();
            } else {
                return Result.error("新增失败");
            }
        } catch (Exception e) {
            LOGGER.error("新增餐饮失败", e);
            return Result.error("新增失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "更新餐饮")
    @PutMapping("/{id}")
    public Result<?> updateRestaurant(@PathVariable Integer id, @RequestBody Restaurant restaurant) {
        try {
            LOGGER.info("更新餐饮，ID：{}", id);
            restaurant.setId(id);
            boolean result = restaurantService.updateById(restaurant);
            if (result) {
                return Result.success();
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            LOGGER.error("更新餐饮失败", e);
            return Result.error("更新失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "删除餐饮")
    @DeleteMapping("/{id}")
    public Result<?> deleteRestaurant(@PathVariable Integer id) {
        try {
            LOGGER.info("删除餐饮，ID：{}", id);
            boolean result = restaurantService.removeById(id);
            if (result) {
                return Result.success();
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            LOGGER.error("删除餐饮失败", e);
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}