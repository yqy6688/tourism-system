package org.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 餐饮订单实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("restaurant_order")
public class RestaurantOrder {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /** 订单号 */
    private String orderNo;
    
    /** 用户ID */
    private Integer userId;
    
    /** 餐饮ID */
    private Integer restaurantId;
    
    /** 预订日期 */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime reservationDate;
    
    /** 用餐人数 */
    private Integer guestCount;
    
    /** 联系人姓名 */
    private String contactName;
    
    /** 联系人电话 */
    private String contactPhone;
    
    /** 备注 */
    private String remark;
    
    /** 总金额（预订金） */
    private BigDecimal totalAmount;
    
    /** 支付方式 */
    private String paymentMethod;
    
    /** 订单状态：PENDING-待支付, PAID-已支付, CANCELLED-已取消, COMPLETED-已完成 */
    private String status;
    
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    /** 支付时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;
    
    /** 取消时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelTime;
    
    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completeTime;
    
    /** 餐饮名称（关联查询） */
    @TableField(exist = false)
    private String restaurantName;
    
    /** 餐饮图片（关联查询） */
    @TableField(exist = false)
    private String restaurantImage;
}