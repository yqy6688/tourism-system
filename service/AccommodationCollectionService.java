package org.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.springboot.entity.Accommodation;

/**
 * 酒店收藏服务接口
 */
public interface AccommodationCollectionService {

    /**
     * 收藏酒店
     * @param userId 用户ID
     * @param accommodationId 酒店ID
     * @return 是否收藏成功
     */
    boolean collectAccommodation(Integer userId, Integer accommodationId);

    /**
     * 取消收藏酒店
     * @param userId 用户ID
     * @param accommodationId 酒店ID
     * @return 是否取消成功
     */
    boolean cancelCollectAccommodation(Integer userId, Integer accommodationId);

    /**
     * 检查用户是否收藏了酒店
     * @param userId 用户ID
     * @param accommodationId 酒店ID
     * @return 是否收藏
     */
    boolean checkCollectionStatus(Integer userId, Integer accommodationId);

    /**
     * 获取用户收藏的酒店列表
     * @param userId 用户ID
     * @param currentPage 当前页码
     * @param size 每页大小
     * @return 酒店列表
     */
    Page<Accommodation> getUserCollections(Integer userId, Integer currentPage, Integer size);

    /**
     * 获取酒店的收藏数量
     * @param accommodationId 酒店ID
     * @return 收藏数量
     */
    Integer getCollectionCount(Integer accommodationId);
}
