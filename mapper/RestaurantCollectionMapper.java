package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.RestaurantCollection;

/**
 * 餐饮收藏Mapper接口
 */
@Mapper
public interface RestaurantCollectionMapper extends BaseMapper<RestaurantCollection> {
}