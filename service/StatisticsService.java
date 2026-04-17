package org.example.springboot.service;

import java.util.Map;

public interface StatisticsService {
    /**
     * 获取基础统计数据
     * @return 基础统计数据
     */
    Map<String, Object> getBasicStatistics();

    /**
     * 获取用户性别统计
     * @return 用户性别统计
     */
    Map<String, Object> getUserSexStatistics();

    /**
     * 获取景点分类统计
     * @return 景点分类统计
     */
    Map<String, Object> getScenicCategoryStatistics();

    /**
     * 获取订单统计
     * @return 订单统计
     */
    Map<String, Object> getOrderStatistics();

    /**
     * 获取景点票价统计
     * @return 景点票价统计
     */
    Map<String, Object> getScenicPriceStatistics();

    /**
     * 获取攻略数量统计
     * @return 攻略数量统计
     */
    Map<String, Object> getGuideCountStatistics();
}
