package org.example.springboot.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 统一订单响应DTO
 * 用于前端订单管理页面展示三种类型的订单
 */
@Data
@Schema(description = "统一订单响应DTO")
public class UnifiedOrderDTO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "订单类型: TICKET-门票, RESTAURANT-餐饮, ACCOMMODATION-酒店")
    private String orderType;

    @Schema(description = "订单类型名称")
    private String orderTypeName;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "商品ID")
    private Long itemId;

    @Schema(description = "商品名称")
    private String itemName;

    @Schema(description = "商品图片")
    private String itemImage;

    @Schema(description = "联系人姓名")
    private String contactName;

    @Schema(description = "联系人电话")
    private String contactPhone;

    @Schema(description = "使用/入住/用餐日期")
    private String useDate;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "订单状态文本")
    private String statusText;

    @Schema(description = "支付方式")
    private String paymentMethod;

    @Schema(description = "支付方式文本")
    private String paymentMethodText;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
