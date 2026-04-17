package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.springboot.entity.RestaurantOrder;

import java.util.List;

/**
 * 餐饮订单Mapper接口
 */
@Mapper
public interface RestaurantOrderMapper extends BaseMapper<RestaurantOrder> {
    
    /**
     * 获取用户餐饮订单列表
     */
    @Select("SELECT ro.*, r.name as restaurantName, r.image_url as restaurantImage " +
            "FROM restaurant_order ro " +
            "LEFT JOIN restaurant r ON ro.restaurant_id = r.id " +
            "WHERE ro.user_id = #{userId} " +
            "ORDER BY ro.create_time DESC")
    List<RestaurantOrder> selectUserOrders(@Param("userId") Integer userId);
}