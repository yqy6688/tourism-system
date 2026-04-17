package org.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.springboot.entity.Restaurant;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 餐饮服务接口
 */
public interface RestaurantService extends IService<Restaurant> {
    
    /**
     * 分页查询餐饮列表
     */
    Page<Restaurant> getRestaurantPage(Integer currentPage, Integer size, String name, String type, 
                                      Integer scenicId, String minPrice, String maxPrice, 
                                      Double minRating, String sortBy,String status);
    
    /**
     * 获取所有餐饮类型
     */
    List<String> getAllTypes();
    
    /**
     * 收藏餐饮
     */
    boolean collectRestaurant(Integer userId, Integer restaurantId);
    
    /**
     * 取消收藏餐饮
     */
    boolean cancelCollectRestaurant(Integer userId, Integer restaurantId);
    
    /**
     * 检查用户是否收藏了餐饮
     */
    boolean isCollected(Integer userId, Integer restaurantId);
    
    /**
     * 获取用户收藏的餐饮列表
     */
    List<Restaurant> getUserCollections(Integer userId);

    Restaurant getRestaurantById(Integer restaurantId);
}