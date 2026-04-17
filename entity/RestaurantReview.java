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
 * 餐饮评价实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("restaurant_review")
public class RestaurantReview {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /** 用户ID */
    private Integer userId;
    
    /** 餐饮ID */
    private Integer restaurantId;
    
    /** 评价内容 */
    private String content;
    
    /** 评分（1-5分） */
    private Double rating;
    
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /** 用户昵称（关联查询） */
    @TableField(exist = false)
    private String nickname;
    
    /** 用户头像（关联查询） */
    @TableField(exist = false)
    private String avatar;
}