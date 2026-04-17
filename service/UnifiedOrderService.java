package org.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.springboot.dto.response.UnifiedOrderDTO;

import java.util.Map;

/**
 * 统一订单服务接口
 */
public interface UnifiedOrderService {

    /**
     * 分页查询所有类型订单
     */
    Page<UnifiedOrderDTO> getAllOrdersByPage(String orderNo, String contactName, String contactPhone,

                                               Integer status, String orderType, Integer currentPage, Integer size);

    /**
     * 获取订单详情
     */
    UnifiedOrderDTO getOrderDetail(String orderType, Long id);

    /**
     * 退款订单
     */
    boolean refundOrder(String orderType, Long id);

    /**
     * 完成订单
     */
    boolean completeOrder(String orderType, Long id);

    /**
     * 删除订单
     */
    boolean deleteOrder(String orderType, Long id);

    /**
     * 获取订单统计信息
     */
    Map<String, Object> getOrderStats();
}
