package org.example.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.springboot.entity.Restaurant;
import org.example.springboot.entity.RestaurantReview;
import org.example.springboot.mapper.RestaurantMapper;
import org.example.springboot.mapper.RestaurantReviewMapper;
import org.example.springboot.service.RestaurantReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 餐饮评价服务实现类
 */
@Service
public class RestaurantReviewServiceImpl extends ServiceImpl<RestaurantReviewMapper, RestaurantReview> implements RestaurantReviewService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantReviewServiceImpl.class);
    
    @Resource
    private RestaurantReviewMapper restaurantReviewMapper;
    
    @Resource
    private RestaurantMapper restaurantMapper;
    
    @Override
    public List<RestaurantReview> getReviewList(Integer restaurantId, Integer currentPage, Integer size) {
        try {
            Integer offset = (currentPage - 1) * size;
            return restaurantReviewMapper.selectReviewList(restaurantId, offset, size);
        } catch (Exception e) {
            LOGGER.error("获取餐饮评价列表失败", e);
            throw new RuntimeException("获取餐饮评价列表失败", e);
        }
    }
    
    @Override
    public Integer getReviewCount(Integer restaurantId) {
        try {
            return restaurantReviewMapper.selectReviewCount(restaurantId);
        } catch (Exception e) {
            LOGGER.error("获取餐饮评价总数失败", e);
            throw new RuntimeException("获取餐饮评价总数失败", e);
        }
    }
    
    @Override
    public Map<Integer, Integer> getRatingStats(Integer restaurantId) {
        try {
            List<RestaurantReviewMapper.RatingStat> stats = restaurantReviewMapper.selectRatingStats(restaurantId);
            Map<Integer, Integer> ratingMap = new HashMap<>();
            
            // 初始化所有星级为0
            for (int i = 1; i <= 5; i++) {
                ratingMap.put(i, 0);
            }
            
            // 填充实际数据
            for (RestaurantReviewMapper.RatingStat stat : stats) {
                int rating = stat.getRating().intValue();
                ratingMap.put(rating, stat.getCount());
            }
            
            return ratingMap;
        } catch (Exception e) {
            LOGGER.error("获取餐饮评分统计失败", e);
            throw new RuntimeException("获取餐饮评分统计失败", e);
        }
    }
    
    @Override
    @Transactional
    public boolean submitReview(RestaurantReview review) {
        try {
            review.setCreateTime(LocalDateTime.now());
            boolean result = this.save(review);
            
            if (result) {
                // 更新餐饮平均评分
                updateRestaurantRating(review.getRestaurantId());
            }
            
            return result;
        } catch (Exception e) {
            LOGGER.error("提交评价失败", e);
            throw new RuntimeException("提交评价失败", e);
        }
    }
    
    /**
     * 更新餐饮平均评分
     */
    private void updateRestaurantRating(Integer restaurantId) {
        try {
            Integer reviewCount = getReviewCount(restaurantId);
            if (reviewCount > 0) {
                // 计算平均评分
                Double averageRating = restaurantReviewMapper.selectAvgRating(restaurantId);
                
                Restaurant restaurant = restaurantMapper.selectById(restaurantId);
                if (restaurant != null) {
                    restaurant.setAverageRating(averageRating);
                    restaurantMapper.updateById(restaurant);
                }
            }
        } catch (Exception e) {
            LOGGER.error("更新餐饮评分失败", e);
        }
    }
    
    /**
     * 在Mapper中添加计算平均评分的方法
     */
    @Resource
    public void setRestaurantReviewMapper(RestaurantReviewMapper restaurantReviewMapper) {
        this.restaurantReviewMapper = restaurantReviewMapper;
    }
}