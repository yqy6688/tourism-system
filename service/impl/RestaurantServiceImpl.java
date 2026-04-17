package org.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.springboot.entity.Restaurant;
import org.example.springboot.entity.RestaurantCollection;
import org.example.springboot.mapper.RestaurantCollectionMapper;
import org.example.springboot.mapper.RestaurantMapper;
import org.example.springboot.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 餐饮服务实现类
 */
@Service
public class RestaurantServiceImpl extends ServiceImpl<RestaurantMapper, Restaurant> implements RestaurantService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantServiceImpl.class);
    
    @Resource
    private RestaurantMapper restaurantMapper;
    
    @Resource
    private RestaurantCollectionMapper restaurantCollectionMapper;
    
    @Override
    public Page<Restaurant> getRestaurantPage(Integer currentPage, Integer size, String name, String type, 
                                              Integer scenicId, String minPrice, String maxPrice, 
                                              Double minRating, String sortBy,String status) {
        try {
            Page<Restaurant> page = new Page<>(currentPage, size);
            
            // 构建查询条件
            QueryWrapper<Restaurant> queryWrapper = new QueryWrapper<>();
            if (status != null && !status.isEmpty()) {
                queryWrapper.eq("status", status);
            }
            
            if (name != null && !name.isEmpty()) {
                queryWrapper.like("name", name);
            }
            
            if (type != null && !type.isEmpty()) {
                queryWrapper.eq("type", type);
            }
            
            if (scenicId != null) {
                queryWrapper.eq("scenic_id", scenicId);
            }
            
            if (minRating != null && minRating > 0) {
                queryWrapper.ge("average_rating", minRating);
            }
            
            // 处理排序
            if (sortBy != null && !sortBy.isEmpty()) {
                switch (sortBy) {
                    case "price_asc":
                        queryWrapper.orderByAsc("price_range");
                        break;
                    case "price_desc":
                        queryWrapper.orderByDesc("price_range");
                        break;
                    case "rating_desc":
                        queryWrapper.orderByDesc("average_rating");
                        break;
                    default:
                        queryWrapper.orderByDesc("create_time");
                        break;
                }
            } else {
                queryWrapper.orderByDesc("create_time");
            }
            
            return this.page(page, queryWrapper);
        } catch (Exception e) {
            LOGGER.error("获取餐饮列表失败", e);
            throw new RuntimeException("获取餐饮列表失败", e);
        }
    }
    
    @Override
    public List<String> getAllTypes() {
        try {
            return restaurantMapper.selectAllTypes();
        } catch (Exception e) {
            LOGGER.error("获取餐饮类型失败", e);
            throw new RuntimeException("获取餐饮类型失败", e);
        }
    }
    
    @Override
    @Transactional
    public boolean collectRestaurant(Integer userId, Integer restaurantId) {
        try {
            // 检查是否已收藏
            QueryWrapper<RestaurantCollection> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("restaurant_id", restaurantId);
            RestaurantCollection existing = restaurantCollectionMapper.selectOne(queryWrapper);
            
            if (existing != null) {
                return true; // 已经收藏过
            }
            
            // 添加收藏
            RestaurantCollection collection = new RestaurantCollection();
            collection.setUserId(userId);
            collection.setRestaurantId(restaurantId);
            collection.setCreateTime(LocalDateTime.now());
            
            int result = restaurantCollectionMapper.insert(collection);
            
            if (result > 0) {
                // 更新餐饮收藏数量
                Restaurant restaurant = this.getById(restaurantId);
                if (restaurant != null) {
                    restaurant.setCollectCount(restaurant.getCollectCount() + 1);
                    this.updateById(restaurant);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.error("收藏餐饮失败", e);
            throw new RuntimeException("收藏餐饮失败", e);
        }
    }
    
    @Override
    @Transactional
    public boolean cancelCollectRestaurant(Integer userId, Integer restaurantId) {
        try {
            QueryWrapper<RestaurantCollection> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("restaurant_id", restaurantId);
            
            int result = restaurantCollectionMapper.delete(queryWrapper);
            
            if (result > 0) {
                // 更新餐饮收藏数量
                Restaurant restaurant = this.getById(restaurantId);
                if (restaurant != null && restaurant.getCollectCount() > 0) {
                    restaurant.setCollectCount(restaurant.getCollectCount() - 1);
                    this.updateById(restaurant);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            LOGGER.error("取消收藏餐饮失败", e);
            throw new RuntimeException("取消收藏餐饮失败", e);
        }
    }
    
    @Override
    public boolean isCollected(Integer userId, Integer restaurantId) {
        try {
            QueryWrapper<RestaurantCollection> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("restaurant_id", restaurantId);
            return restaurantCollectionMapper.selectOne(queryWrapper) != null;
        } catch (Exception e) {
            LOGGER.error("检查收藏状态失败", e);
            return false;
        }
    }
    
    @Override
    public List<Restaurant> getUserCollections(Integer userId) {
        try {
            QueryWrapper<RestaurantCollection> collectionWrapper = new QueryWrapper<>();
            collectionWrapper.eq("user_id", userId);
            List<RestaurantCollection> collections = restaurantCollectionMapper.selectList(collectionWrapper);
            
            if (collections.isEmpty()) {
                return List.of();
            }
            
            List<Integer> restaurantIds = collections.stream()
                    .map(RestaurantCollection::getRestaurantId)
                    .toList();
            
            QueryWrapper<Restaurant> restaurantWrapper = new QueryWrapper<>();
            restaurantWrapper.in("id", restaurantIds);
            return this.list(restaurantWrapper);
        } catch (Exception e) {
            LOGGER.error("获取用户收藏列表失败", e);
            throw new RuntimeException("获取用户收藏列表失败", e);
        }
    }
    /**
     * 根据ID获取餐饮
     * @param restaurantId 餐饮ID
     * @return 餐饮对象
     */

    @Override
    public Restaurant getRestaurantById(Integer restaurantId) {
        return restaurantMapper.selectById(restaurantId);
    }
}