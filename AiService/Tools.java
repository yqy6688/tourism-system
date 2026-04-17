package org.example.springboot.AiService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.entity.ScenicCategory;
import org.example.springboot.entity.ScenicSpot;
import org.example.springboot.service.ScenicCategoryService;
import org.example.springboot.service.ScenicSpotService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI景点推荐工具类
 * 
 * 职责：为AI提供查询景点数据库的工具函数
 * 
 * @author AI Assistant
 */
@Slf4j
@Component
public class Tools {

    @Resource
    private ScenicSpotService scenicSpotService;
    
    @Resource
    private ScenicCategoryService scenicCategoryService;

    public String searchScenicSpots(
        Integer page,
        String location,
        BigDecimal minPrice,
        BigDecimal maxPrice
    ) {
        try {
            // 固定每页20条
            final int PAGE_SIZE = 20;
            int currentPage = (page != null && page > 0) ? page : 1;
            
            log.info("AI工具调用: 分页搜索景点, page={}, pageSize={}, location={}, minPrice={}, maxPrice={}",
                    currentPage, PAGE_SIZE, location, minPrice, maxPrice);
            
            // 单次分页查询（不传入关键词）
            Page<ScenicSpot> pageResult = scenicSpotService.getScenicSpotsByPage(
                null, // 不使用关键词搜索
                location,
                null, // categoryId
                null, // sortBy
                currentPage,
                PAGE_SIZE
            );
            
            List<ScenicSpot> spots = pageResult.getRecords();
            
            // 如果有价格筛选，进行二次过滤
            if (minPrice != null || maxPrice != null) {
                spots = spots.stream()
                    .filter(spot -> {
                        BigDecimal price = spot.getPrice();
                        if (price == null) return false;
                        
                        boolean matchMin = minPrice == null || price.compareTo(minPrice) >= 0;
                        boolean matchMax = maxPrice == null || price.compareTo(maxPrice) <= 0;
                        
                        return matchMin && matchMax;
                    })
                    .collect(Collectors.toList());
            }
            
            // 计算分页信息
            long totalRecords = pageResult.getTotal();
            long totalPages = (totalRecords + PAGE_SIZE - 1) / PAGE_SIZE;
            
            if (spots.isEmpty()) {
                if (currentPage > totalPages && totalPages > 0) {
                    return String.format("第%d页已超出总页数（共%d页）。没有更多数据了。", 
                            currentPage, totalPages);
                }
                return "当前页没有找到符合条件的景点。";
            }
            
            // 格式化返回结果
            StringBuilder result = new StringBuilder();
            result.append(String.format("=== 第%d页（共%d页，总计%d个景点） ===\n\n", 
                    currentPage, totalPages, totalRecords));
            
            for (int i = 0; i < spots.size(); i++) {
                ScenicSpot spot = spots.get(i);
                result.append(String.format("%d. **%s**\n", i + 1, spot.getName()));
                result.append(String.format("   📍 位置：%s\n", spot.getLocation() != null ? spot.getLocation() : "未知"));
                
                if (spot.getPrice() != null) {
                    if (spot.getPrice().compareTo(BigDecimal.ZERO) == 0) {
                        result.append("   💰 门票：免费\n");
                    } else {
                        result.append(String.format("   💰 门票：¥%.2f\n", spot.getPrice()));
                    }
                }
                
                if (spot.getCategoryInfo() != null) {
                    result.append(String.format("   🏷️ 分类：%s\n", spot.getCategoryInfo().getName()));
                }
                
                if (spot.getOpeningHours() != null) {
                    result.append(String.format("   ⏰ 开放时间：%s\n", spot.getOpeningHours()));
                }
                
                if (spot.getDescription() != null) {
                    String desc = spot.getDescription().length() > 100 
                        ? spot.getDescription().substring(0, 100) + "..." 
                        : spot.getDescription();
                    result.append(String.format("   📝 简介：%s\n", desc));
                }
                
                result.append(String.format("   🆔 景点ID：%d\n", spot.getId()));
                result.append("\n");
            }
            
            // 添加分页导航提示
            if (currentPage < totalPages) {
                result.append(String.format("\n💡 提示：还有%d页数据未查看，可以调用 searchScenicSpots(page=%d) 继续查询下一页。", 
                        totalPages - currentPage, currentPage + 1));
            } else {
                result.append("\n✅ 已到最后一页，没有更多数据了。");
            }
            
            log.info("景点搜索完成，第{}页，返回{}条结果，共{}页", currentPage, spots.size(), totalPages);
            return result.toString();
            
        } catch (Exception e) {
            log.error("搜索景点失败: {}", e.getMessage(), e);
            return "搜索景点时发生错误，请稍后重试或联系管理员。错误信息：" + e.getMessage();
        }
    }

    public String getScenicSpotDetail(Long scenicSpotId) {
        try {
            log.info("AI工具调用: 获取景点详情, scenicSpotId={}", scenicSpotId);
            
            ScenicSpot spot = scenicSpotService.getById(scenicSpotId);
            
            if (spot == null) {
                return String.format("抱歉，没有找到ID为 %d 的景点。请确认景点ID是否正确。", scenicSpotId);
            }
            
            // 格式化详细信息
            StringBuilder detail = new StringBuilder();
            detail.append(String.format("# %s 详细信息\n\n", spot.getName()));
            
            detail.append(String.format("📍 **位置**：%s\n\n", spot.getLocation() != null ? spot.getLocation() : "未知"));
            
            if (spot.getPrice() != null) {
                if (spot.getPrice().compareTo(BigDecimal.ZERO) == 0) {
                    detail.append("💰 **门票**：免费\n\n");
                } else {
                    detail.append(String.format("💰 **门票**：¥%.2f\n\n", spot.getPrice()));
                }
            }
            
            if (spot.getCategoryInfo() != null) {
                detail.append(String.format("🏷️ **分类**：%s\n\n", spot.getCategoryInfo().getName()));
            }
            
            if (spot.getOpeningHours() != null) {
                detail.append(String.format("⏰ **开放时间**：%s\n\n", spot.getOpeningHours()));
            }
            
            if (spot.getDescription() != null) {
                detail.append(String.format("📝 **详细介绍**：\n%s\n\n", spot.getDescription()));
            }
            
            if (spot.getLatitude() != null && spot.getLongitude() != null) {
                detail.append(String.format("🗺️ **地理坐标**：纬度 %.6f, 经度 %.6f\n\n", 
                    spot.getLatitude(), spot.getLongitude()));
            }
            
            detail.append(String.format("🆔 **景点ID**：%d\n", spot.getId()));
            
            log.info("景点详情获取成功: {}", spot.getName());
            return detail.toString();
            
        } catch (Exception e) {
            log.error("获取景点详情失败: {}", e.getMessage(), e);
            return String.format("获取景点详情时发生错误：%s", e.getMessage());
        }
    }

    public String getAllCategories() {
        try {
            log.info("AI工具调用: 获取所有景点分类");
            
            List<ScenicCategory> categories = scenicCategoryService.getCategoryTree();
            
            if (categories.isEmpty()) {
                return "系统中暂无景点分类数据。";
            }
            
            StringBuilder result = new StringBuilder();
            result.append("系统中的景点分类如下：\n\n");
            
            for (ScenicCategory category : categories) {
                result.append(formatCategory(category, 0));
            }
            
            result.append("\n提示：您可以使用分类ID进行精确搜索，例如'搜索分类ID为1的景点'。");
            
            log.info("景点分类获取成功，共{}个分类", categories.size());
            return result.toString();
            
        } catch (Exception e) {
            log.error("获取景点分类失败: {}", e.getMessage(), e);
            return "获取景点分类时发生错误：" + e.getMessage();
        }
    }
    
    /**
     * 递归格式化分类（树形结构）
     */
    private String formatCategory(ScenicCategory category, int level) {
        StringBuilder sb = new StringBuilder();
        
        // 添加缩进
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
        
        // 添加分类信息
        sb.append(String.format("- **%s** (ID: %d)", category.getName(), category.getId()));
        
        if (category.getDescription() != null) {
            sb.append(String.format(" - %s", category.getDescription()));
        }
        sb.append("\n");
        
        // 递归处理子分类
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            for (ScenicCategory child : category.getChildren()) {
                sb.append(formatCategory(child, level + 1));
            }
        }
        
        return sb.toString();
    }
}
