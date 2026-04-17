package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.AccommodationCollection;

/**
 * 酒店收藏Mapper
 */
@Mapper
public interface AccommodationCollectionMapper extends BaseMapper<AccommodationCollection> {
}
