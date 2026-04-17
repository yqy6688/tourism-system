package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Accommodation;
import org.example.springboot.service.AccommodationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "酒店管理接口")
@RestController
@RequestMapping("/accommodation")
public class AccommodationController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AccommodationController.class);
    
    @Resource
    private AccommodationService accommodationService;
    
    @Operation(summary = "分页查询酒店列表")
    @GetMapping("/page")
    public Result<?> getAccommodationsByPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer scenicId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            @RequestParam(required = false) String minRating,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            LOGGER.info("分页查询酒店列表，参数：name={}, scenicId={}, type={}, price={}~{}, starLevel>={}, sortBy={}, page={}, size={}", 
                        name, scenicId, type, minPrice, maxPrice, minRating, sortBy, currentPage, size);
            
            Page<Accommodation> page = accommodationService.getAccommodationsByPage(
                    name, scenicId, type, minPrice, maxPrice, minRating, sortBy, currentPage, size);
            
            return Result.success(page);
        } catch (Exception e) {
            LOGGER.error("查询酒店列表失败", e);
            return Result.error("查询酒店列表失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取酒店详情")
    @GetMapping("/{id}")
    public Result<?> getAccommodationById(@PathVariable Integer id) {
        try {
            LOGGER.info("获取酒店详情，id={}", id);
            
            Accommodation accommodation = accommodationService.getAccommodationById(id);
            
            if (accommodation == null) {
                return Result.error("酒店信息不存在");
            }
            
            return Result.success(accommodation);
        } catch (Exception e) {
            LOGGER.error("获取酒店详情失败", e);
            return Result.error("获取酒店详情失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "添加酒店信息")
    @PostMapping
    public Result<?> addAccommodation(@RequestBody Accommodation accommodation) {
        try {
            LOGGER.info("添加酒店信息：{}", accommodation);
            
            boolean result = accommodationService.addAccommodation(accommodation);
            
            if (result) {
                return Result.success(accommodation);
            } else {
                return Result.error("添加酒店信息失败");
            }
        } catch (Exception e) {
            LOGGER.error("添加酒店信息失败", e);
            return Result.error("添加酒店信息失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "更新酒店信息")
    @PutMapping("/{id}")
    public Result<?> updateAccommodation(@PathVariable Long id, @RequestBody Accommodation accommodation) {
        try {
            LOGGER.info("更新酒店信息，id={}，数据：{}", id, accommodation);
            
            accommodation.setId(id);
            boolean result = accommodationService.updateAccommodation(accommodation);
            
            if (result) {
                return Result.success(accommodation);
            } else {
                return Result.error("更新酒店信息失败");
            }
        } catch (Exception e) {
            LOGGER.error("更新酒店信息失败", e);
            return Result.error("更新酒店信息失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "删除酒店信息")
    @DeleteMapping("/{id}")
    public Result<?> deleteAccommodation(@PathVariable Integer id) {
        try {
            LOGGER.info("删除酒店信息，id={}", id);
            
            boolean result = accommodationService.deleteAccommodation(id);
            
            if (result) {
                return Result.success();
            } else {
                return Result.error("删除酒店信息失败");
            }
        } catch (Exception e) {
            LOGGER.error("删除酒店信息失败", e);
            return Result.error("删除酒店信息失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取酒店类型列表")
    @GetMapping("/types")
    public Result<?> getAccommodationTypes() {
        try {
            List<String> types = accommodationService.getAccommodationTypes();
            return Result.success(types);
        } catch (Exception e) {
            LOGGER.error("获取酒店类型列表失败", e);
            return Result.error("获取酒店类型列表失败：" + e.getMessage());
        }
    }
    @Operation(summary = "收藏酒店")
    @PutMapping("/collect")
    public Result<?> collectAccommodation(@RequestBody Map<String, Object> params) {
        try {
            Integer userId = (Integer) params.get("userId");
            Integer accommodationId = (Integer) params.get("accommodationId");
            
            LOGGER.info("用户{}收藏酒店{}", userId, accommodationId);
            boolean result = accommodationService.collectAccommodation(userId, accommodationId);
            if (result) {
                return Result.success();
            } else {
                return Result.error("收藏失败");
            }
        } catch (Exception e) {
            LOGGER.error("收藏酒店失败", e);
            return Result.error("收藏失败：" + e.getMessage());
        }
    }
    @Operation(summary = "取消收藏酒店")
    @DeleteMapping("/collect")
    public Result<?> cancelCollectAccommodation(
            @RequestBody Map<String, Object> params) {
        try {
            Integer userId = (Integer) params.get("userId");
            Integer accommodationId = (Integer) params.get("accommodationId");
            LOGGER.info("用户{}取消收藏酒店{}", userId, accommodationId);
            boolean result = accommodationService.cancelCollectAccommodation(userId, accommodationId);
            if (result) {
                return Result.success();
            } else {
                return Result.error("取消收藏失败");
            }
        } catch (Exception e) {
            LOGGER.error("取消收藏酒店失败", e);
            return Result.error("取消收藏失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取用户收藏列表")
    @GetMapping("/collect/user/{userId}")
    public Result<?> getUserCollections(@PathVariable Integer userId,
                                       @RequestParam(defaultValue = "1") Integer currentPage,
                                       @RequestParam(defaultValue = "12") Integer size) {
        try {
            LOGGER.info("获取用户{}的酒店收藏列表，页码：{}，每页大小：{}", userId, currentPage, size);
            List<Accommodation> collections = accommodationService.getUserCollections(userId);
            return Result.success(collections);
        } catch (Exception e) {
            LOGGER.error("获取用户收藏列表失败", e);
            return Result.error("获取收藏列表失败：" + e.getMessage());
        }
    }

    @Operation(summary = "检查用户是否收藏了酒店")
    @GetMapping("/collect/check")
    public Result<?> checkCollectionStatus(@RequestParam Integer userId, @RequestParam Integer accommodationId) {
        try {
            LOGGER.info("检查用户{}是否收藏了酒店{}", userId, accommodationId);
            boolean status = accommodationService.checkCollectionStatus(userId, accommodationId);
            return Result.success(status);
        } catch (Exception e) {
            LOGGER.error("检查收藏状态失败", e);
            return Result.error("检查收藏状态失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取酒店收藏数量")
    @GetMapping("/collect/count/{accommodationId}")
    public Result<?> getCollectionCount(@PathVariable Integer accommodationId) {
        try {
            LOGGER.info("获取酒店{}的收藏数量", accommodationId);
            Integer count = accommodationService.getCollectionCount(accommodationId);
            return Result.success(count);
        } catch (Exception e) {
            LOGGER.error("获取收藏数量失败", e);
            return Result.error("获取收藏数量失败：" + e.getMessage());
        }
    }
    
    @Operation(summary = "获取酒店房型列表")
    @GetMapping("/{id}/rooms")
    public Result<?> getAccommodationRooms(@PathVariable Integer id) {
        try {
            LOGGER.info("获取酒店房型列表，酒店ID：{}", id);
            List<Map<String, Object>> rooms = accommodationService.getAccommodationRooms(id);
            return Result.success(rooms);
        } catch (Exception e) {
            LOGGER.error("获取酒店房型列表失败", e);
            return Result.error("获取房型列表失败：" + e.getMessage());
        }
    }
}