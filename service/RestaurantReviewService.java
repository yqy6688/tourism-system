package org.example.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.springboot.entity.RestaurantReview;

import java.util.List;
import java.util.Map;

/**
 * 餐饮评价服务接口
 */
public interface RestaurantReviewService extends IService<RestaurantReview> {
    
    /**
     * 获取餐饮评价列表
     */
    List<RestaurantReview> getReviewList(Integer restaurantId, Integer currentPage, Integer size);
    
    /**
     * 获取餐饮评价总数
     */
    Integer getReviewCount(Integer restaurantId);
    
    /**
     * 获取餐饮评分统计
     */
    Map<Integer, Integer> getRatingStats(Integer restaurantId);
    
    /**
     * 提交评价
     */
    boolean submitReview(RestaurantReview review);
}