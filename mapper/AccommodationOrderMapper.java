package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.springboot.entity.AccommodationOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 酒店订单Mapper
 */
@Mapper
public interface AccommodationOrderMapper extends BaseMapper<AccommodationOrder> {
}
