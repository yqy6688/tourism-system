package org.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.example.springboot.mapper.*;
import org.example.springboot.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private ScenicSpotMapper scenicSpotMapper;

    @Resource
    private AccommodationMapper accommodationMapper;

    @Resource
    private RestaurantMapper restaurantMapper;

    @Resource
    private TicketOrderMapper ticketOrderMapper;

    @Resource
    private AccommodationOrderMapper accommodationOrderMapper;

    @Resource
    private RestaurantOrderMapper restaurantOrderMapper;

    @Resource
    private TravelGuideMapper travelGuideMapper;

    @Resource
    private ScenicCategoryMapper scenicCategoryMapper;

    @Override
    public Map<String, Object> getBasicStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        try {
            // 统计用户数量
            long userCount = userMapper.selectCount(null);
            statistics.put("userCount", userCount);

            // 统计景点数量
            long scenicCount = scenicSpotMapper.selectCount(null);
            statistics.put("scenicCount", scenicCount);

            // 统计酒店数量
            long accommodationCount = accommodationMapper.selectCount(null);
            statistics.put("accommodationCount", accommodationCount);

            // 统计餐饮数量
            long restaurantCount = restaurantMapper.selectCount(null);
            statistics.put("restaurantCount", restaurantCount);

            // 统计商家数量（角色为 MERCHANT）
            LambdaQueryWrapper<org.example.springboot.entity.User> merchantWrapper = new LambdaQueryWrapper<>();
            merchantWrapper.eq(org.example.springboot.entity.User::getRoleCode, "MERCHANT");
            long merchantCount = userMapper.selectCount(merchantWrapper);
            statistics.put("merchantCount", merchantCount);

            // 统计攻略数量
            long guideCount = travelGuideMapper.selectCount(null);
            statistics.put("guideCount", guideCount);

            LOGGER.info("基础统计数据：用户={}, 景点={}, 酒店={}, 餐饮={}, 商家={}, 攻略={}",
                    userCount, scenicCount, accommodationCount, restaurantCount, merchantCount, guideCount);

        } catch (Exception e) {
            LOGGER.error("获取基础统计数据失败", e);
            // 失败时返回默认值
            statistics.put("userCount", 0);
            statistics.put("scenicCount", 0);
            statistics.put("accommodationCount", 0);
            statistics.put("restaurantCount", 0);
            statistics.put("merchantCount", 0);
            statistics.put("guideCount", 0);
        }

        return statistics;
    }

    @Override
    public Map<String, Object> getUserSexStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        try {
            // 统计用户性别分布
            List<Map<String, Object>> sexList = userMapper.selectMaps(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<org.example.springboot.entity.User>()
                    .select("sex, count(*) as count")
                    .groupBy("sex"));

            List<String> labels = new ArrayList<>();
            List<Integer> data = new ArrayList<>();

            for (Map<String, Object> item : sexList) {
                String sex = (String) item.get("sex");
                if (sex == null) {
                    sex = "未知";
                }
                labels.add(sex);
                data.add(((Number) item.get("count")).intValue());
            }

            statistics.put("labels", labels);
            statistics.put("data", data);

            LOGGER.info("用户性别统计：{}", statistics);

        } catch (Exception e) {
            LOGGER.error("获取用户性别统计失败", e);
            // 失败时返回空数据
            statistics.put("labels", new ArrayList<>());
            statistics.put("data", new ArrayList<>());
        }

        return statistics;
    }

    @Override
    public Map<String, Object> getScenicCategoryStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        try {
            // 统计景点分类分布
            List<Map<String, Object>> categoryList = scenicSpotMapper.selectMaps(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<org.example.springboot.entity.ScenicSpot>()
                    .select("category_id, count(*) as count")
                    .groupBy("category_id"));

            List<String> labels = new ArrayList<>();
            List<Integer> data = new ArrayList<>();

            for (Map<String, Object> item : categoryList) {
                Long categoryId = (Long) item.get("category_id");
                // 查询分类名称
                org.example.springboot.entity.ScenicCategory category = scenicCategoryMapper.selectById(categoryId);
                String categoryName = category != null ? category.getName() : "未知";
                labels.add(categoryName);
                data.add(((Number) item.get("count")).intValue());
            }

            statistics.put("labels", labels);
            statistics.put("data", data);

            LOGGER.info("景点分类统计：{}", statistics);

        } catch (Exception e) {
            LOGGER.error("获取景点分类统计失败", e);
            // 失败时返回空数据
            statistics.put("labels", new ArrayList<>());
            statistics.put("data", new ArrayList<>());
        }

        return statistics;
    }

    @Override
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        try {
            // 生成最近12个月的月份列表
            List<String> months = new ArrayList<>();
            LocalDate now = LocalDate.now();
            for (int i = 11; i >= 0; i--) {
                LocalDate month = now.minusMonths(i);
                months.add(month.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            }

            // 统计每月门票订单数量
            List<Integer> ticketOrderData = new ArrayList<>();
            for (String month : months) {
                String startDate = month + "-01";
                String endDate = LocalDate.parse(startDate).plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                LambdaQueryWrapper<org.example.springboot.entity.TicketOrder> wrapper = new LambdaQueryWrapper<>();
                wrapper.between(org.example.springboot.entity.TicketOrder::getCreateTime, startDate, endDate);
                long count = ticketOrderMapper.selectCount(wrapper);
                ticketOrderData.add((int) count);
            }

            // 统计每月酒店订单数量
            List<Integer> accommodationOrderData = new ArrayList<>();
            for (String month : months) {
                String startDate = month + "-01";
                String endDate = LocalDate.parse(startDate).plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                LambdaQueryWrapper<org.example.springboot.entity.AccommodationOrder> wrapper = new LambdaQueryWrapper<>();
                wrapper.between(org.example.springboot.entity.AccommodationOrder::getCreateTime, startDate, endDate);
                long count = accommodationOrderMapper.selectCount(wrapper);
                accommodationOrderData.add((int) count);
            }

            // 统计每月餐饮订单数量
            List<Integer> restaurantOrderData = new ArrayList<>();
            for (String month : months) {
                String startDate = month + "-01";
                String endDate = LocalDate.parse(startDate).plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                LambdaQueryWrapper<org.example.springboot.entity.RestaurantOrder> wrapper = new LambdaQueryWrapper<>();
                wrapper.between(org.example.springboot.entity.RestaurantOrder::getCreateTime, startDate, endDate);
                long count = restaurantOrderMapper.selectCount(wrapper);
                restaurantOrderData.add((int) count);
            }

            statistics.put("months", months);
            statistics.put("ticketOrderData", ticketOrderData);
            statistics.put("accommodationOrderData", accommodationOrderData);
            statistics.put("restaurantOrderData", restaurantOrderData);

            LOGGER.info("订单统计：月份={}, 门票订单={}, 酒店订单={}, 餐饮订单={}",
                    months, ticketOrderData, accommodationOrderData, restaurantOrderData);

        } catch (Exception e) {
            LOGGER.error("获取订单统计失败", e);
            // 失败时返回空数据
            statistics.put("months", new ArrayList<>());
            statistics.put("ticketOrderData", new ArrayList<>());
            statistics.put("accommodationOrderData", new ArrayList<>());
            statistics.put("restaurantOrderData", new ArrayList<>());
        }

        return statistics;
    }

    @Override
    public Map<String, Object> getScenicPriceStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        try {
            // 查询景点名称和票价
            List<org.example.springboot.entity.ScenicSpot> scenicSpots = scenicSpotMapper.selectList(new LambdaQueryWrapper<org.example.springboot.entity.ScenicSpot>()
                    .select(org.example.springboot.entity.ScenicSpot::getName, org.example.springboot.entity.ScenicSpot::getPrice)
                    .orderByDesc(org.example.springboot.entity.ScenicSpot::getPrice)
                    .last("LIMIT 10")); // 只取前10个景点

            List<String> labels = new ArrayList<>();
            List<Double> data = new ArrayList<>();

            for (org.example.springboot.entity.ScenicSpot spot : scenicSpots) {
                labels.add(spot.getName());
                data.add(spot.getPrice().doubleValue());
            }

            statistics.put("labels", labels);
            statistics.put("data", data);

            LOGGER.info("景点票价统计：{}", statistics);

        } catch (Exception e) {
            LOGGER.error("获取景点票价统计失败", e);
            // 失败时返回空数据
            statistics.put("labels", new ArrayList<>());
            statistics.put("data", new ArrayList<>());
        }

        return statistics;
    }

    @Override
    public Map<String, Object> getGuideCountStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        try {
            // 生成最近12个月的月份列表
            List<String> months = new ArrayList<>();
            LocalDate now = LocalDate.now();
            for (int i = 11; i >= 0; i--) {
                LocalDate month = now.minusMonths(i);
                months.add(month.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            }

            // 统计每月攻略数量
            List<Integer> guideCountData = new ArrayList<>();
            for (String month : months) {
                String startDate = month + "-01";
                String endDate = LocalDate.parse(startDate).plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                LambdaQueryWrapper<org.example.springboot.entity.TravelGuide> wrapper = new LambdaQueryWrapper<>();
                wrapper.between(org.example.springboot.entity.TravelGuide::getCreateTime, startDate, endDate);
                long count = travelGuideMapper.selectCount(wrapper);
                guideCountData.add((int) count);
            }

            statistics.put("months", months);
            statistics.put("guideCountData", guideCountData);

            LOGGER.info("攻略数量统计：月份={}, 数量={}", months, guideCountData);

        } catch (Exception e) {
            LOGGER.error("获取攻略数量统计失败", e);
            // 失败时返回空数据
            statistics.put("months", new ArrayList<>());
            statistics.put("guideCountData", new ArrayList<>());
        }

        return statistics;
    }
}
