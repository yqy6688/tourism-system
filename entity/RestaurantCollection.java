package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 餐饮收藏实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("restaurant_collection")
public class RestaurantCollection {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /** 用户ID */
    private Integer userId;
    
    /** 餐饮ID */
    private Integer restaurantId;
    
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}