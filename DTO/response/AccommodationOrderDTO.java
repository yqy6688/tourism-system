package org.example.springboot.DTO.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: LZL
 * @CreateTime: 2026-03-16
 * @Description: 酒店订单
 * @Version: 1.0
 */
@Data
public class AccommodationOrderDTO {

    @TableId(type = IdType.AUTO)
    @Schema(description = "订单ID")
    private Integer id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Integer userId;

    @Schema(description = "酒店ID")
    private Integer accommodationId;
    @Schema(description = "酒店名称")
    private String accommodationName;

    @Schema(description = "房型ID")
    private Integer roomId;

    @Schema(description = "入住日期")
    private LocalDateTime checkInDate;

    @Schema(description = "退房日期")
    private LocalDateTime checkOutDate;

    @Schema(description = "入住人数")
    private Integer guestCount;

    @Schema(description = "联系人姓名")
    private String contactName;

    @Schema(description = "联系人电话")
    private String contactPhone;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "入住天数")
    private Integer days;

    @Schema(description = "总金额")
    private Double totalAmount;

    @Schema(description = "支付方式")
    private String paymentMethod;

    @Schema(description = "订单状态")
    private String status; // PENDING, PAID, CANCELLED, COMPLETED

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;

    @Schema(description = "完成时间")
    private LocalDateTime completeTime;
}
