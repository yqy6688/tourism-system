package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.springboot.entity.RestaurantReview;

import java.util.List;

/**
 * 餐饮评价Mapper接口
 */
@Mapper
public interface RestaurantReviewMapper extends BaseMapper<RestaurantReview> {
    
    /**
     * 获取餐饮评价列表（带用户信息）
     */
    @Select("SELECT rr.*, u.nickname, u.avatar " +
            "FROM restaurant_review rr " +
            "LEFT JOIN user u ON rr.user_id = u.id " +
            "WHERE rr.restaurant_id = #{restaurantId} " +
            "ORDER BY rr.create_time DESC " +
            "LIMIT #{offset}, #{size}")
    List<RestaurantReview> selectReviewList(@Param("restaurantId") Integer restaurantId,
                                           @Param("offset") Integer offset,
                                           @Param("size") Integer size);
    
    /**
     * 获取餐饮评价总数
     */
    @Select("SELECT COUNT(*) FROM restaurant_review WHERE restaurant_id = #{restaurantId}")
    Integer selectReviewCount(@Param("restaurantId") Integer restaurantId);
    
    /**
     * 获取餐饮评分统计
     */
    @Select("SELECT rating, COUNT(*) as count " +
            "FROM restaurant_review " +
            "WHERE restaurant_id = #{restaurantId} " +
            "GROUP BY rating")
    List<RatingStat> selectRatingStats(@Param("restaurantId") Integer restaurantId);
    
    /**
     * 计算平均评分
     */
    @Select("SELECT AVG(rating) FROM restaurant_review WHERE restaurant_id = #{restaurantId}")
    Double selectAvgRating(@Param("restaurantId") Integer restaurantId);
    
    /**
     * 评分统计结果类
     */
    class RatingStat {
        private Double rating;
        private Integer count;
        
        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }
}