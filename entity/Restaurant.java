package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 餐饮信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("restaurant")
public class Restaurant {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /** 餐饮名称 */
    private String name;
    
    /** 餐饮类型 */
    private String type;
    
    /** 地址 */
    private String address;
    
    /** 关联景点ID */
    private Integer scenicId;
    
    /** 餐饮描述 */
    private String description;
    
    /** 联系电话 */
    private String contactPhone;
    
    /** 价格区间 */
    private String priceRange;
    
    /** 平均评分 */
    private Double averageRating;
    
    /** 主图URL */
    private String imageUrl;
    
    /** 图片列表，JSON格式存储多张图片URL */
    private String imageList;
    
    /** 特色菜品 */
    private String features;
    
    /** 距景点距离 */
    private String distance;
    
    /** 状态：OPEN-营业中, CLOSED-休息中, OFFLINE-已下架 */
    private String status;
    
    /** 收藏数量 */
    private Integer collectCount;
    
    /** 营业时间 */
    private String businessHours;
    
    /** 容纳人数 */
    private Integer capacity;
    
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    /** 景点名称（关联查询） */
    @TableField(exist = false)
    private String scenicName;
}