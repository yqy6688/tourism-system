package org.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.springboot.entity.Accommodation;
import org.example.springboot.entity.AccommodationCollection;
import org.example.springboot.mapper.AccommodationCollectionMapper;
import org.example.springboot.mapper.AccommodationMapper;
import org.example.springboot.service.AccommodationCollectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

/**
 * 酒店收藏服务实现
 */
@Service
public class AccommodationCollectionServiceImpl implements AccommodationCollectionService {

    @Resource
    private AccommodationCollectionMapper accommodationCollectionMapper;

    @Resource
    private AccommodationMapper accommodationMapper;

    @Override
    @Transactional
    public boolean collectAccommodation(Integer userId, Integer accommodationId) {
        // 检查是否已经收藏
        QueryWrapper<AccommodationCollection> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("accommodation_id", accommodationId);
        if (accommodationCollectionMapper.selectOne(wrapper) != null) {
            return true; // 已经收藏过
        }

        // 创建收藏记录
        AccommodationCollection collection = new AccommodationCollection();
        collection.setUserId(userId);
        collection.setAccommodationId(accommodationId);
        collection.setCollectTime(LocalDateTime.now());
        collection.setCreateTime(LocalDateTime.now());
        collection.setUpdateTime(LocalDateTime.now());
        int insert = accommodationCollectionMapper.insert(collection);
        if (insert > 0){
            Accommodation accommodation = accommodationMapper.selectById(accommodationId);
            if (accommodation != null){
                accommodation.setCollectCount(accommodation.getCollectCount() + 1);
                return accommodationMapper.updateById(accommodation) > 0;
            }
        }

        return false;
    }

    @Override
    @Transactional
    public boolean cancelCollectAccommodation(Integer userId, Integer accommodationId) {
        QueryWrapper<AccommodationCollection> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("accommodation_id", accommodationId);
        int delete = accommodationCollectionMapper.delete(wrapper);
        if (delete>0){
            Accommodation accommodation = accommodationMapper.selectById(accommodationId);
            if (accommodation != null){
                accommodation.setCollectCount(accommodation.getCollectCount() -1);
                return accommodationMapper.updateById(accommodation) > 0;
            }
        }
        return false;
    }

    @Override
    public boolean checkCollectionStatus(Integer userId, Integer accommodationId) {
        QueryWrapper<AccommodationCollection> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("accommodation_id", accommodationId);
        return accommodationCollectionMapper.selectOne(wrapper) != null;
    }

    @Override
    public Page<Accommodation> getUserCollections(Integer userId, Integer currentPage, Integer size) {
        // 使用MyBatis-Plus的分页查询
        Page<Accommodation> page = new Page<>(currentPage, size);

        // 查询用户收藏的酒店ID列表
        QueryWrapper<AccommodationCollection> collectionWrapper = new QueryWrapper<>();
        collectionWrapper.eq("user_id", userId)
                .orderByDesc("collect_time");

        // 这里需要使用关联查询，查询用户收藏的酒店详情
        // 实际项目中可能需要使用XML映射文件或其他方式实现关联查询
        // 这里简化处理，假设直接查询酒店信息
        QueryWrapper<Accommodation> accommodationWrapper = new QueryWrapper<>();
        accommodationWrapper.inSql("id", "SELECT accommodation_id FROM accommodation_collection WHERE user_id = " + userId);

        return accommodationMapper.selectPage(page, accommodationWrapper);
    }

    @Override
    public Integer getCollectionCount(Integer accommodationId) {
        QueryWrapper<AccommodationCollection> wrapper = new QueryWrapper<>();
        wrapper.eq("accommodation_id", accommodationId);
        return Math.toIntExact(accommodationCollectionMapper.selectCount(wrapper));
    }
}
