package org.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.springboot.dto.response.UnifiedOrderDTO;
import org.example.springboot.entity.*;
import org.example.springboot.mapper.*;
import org.example.springboot.service.UnifiedOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 统一订单服务实现类
 */
@Service
public class UnifiedOrderServiceImpl implements UnifiedOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnifiedOrderServiceImpl.class);

    @Resource
    private TicketOrderMapper ticketOrderMapper;

    @Resource
    private RestaurantOrderMapper restaurantOrderMapper;

    @Resource
    private AccommodationOrderMapper accommodationOrderMapper;

    @Resource
    private TicketMapper ticketMapper;

    @Resource
    private ScenicSpotMapper scenicSpotMapper;

    @Resource
    private RestaurantMapper restaurantMapper;

    @Resource
    private AccommodationMapper accommodationMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public Page<UnifiedOrderDTO> getAllOrdersByPage(String orderNo, String contactName, String contactPhone,
                                                     Integer status, String orderType, Integer currentPage, Integer size) {

        List<UnifiedOrderDTO> allOrders = new ArrayList<>();

        // 根据订单类型查询
        if (orderType == null || orderType.isEmpty() || "TICKET".equals(orderType)) {
            allOrders.addAll(getTicketOrders(orderNo, contactName, contactPhone, status));
        }

        if (orderType == null || orderType.isEmpty() || "RESTAURANT".equals(orderType)) {
            allOrders.addAll(getRestaurantOrders(orderNo, contactName, contactPhone, status));
        }

        if (orderType == null || orderType.isEmpty() || "ACCOMMODATION".equals(orderType)) {
            allOrders.addAll(getAccommodationOrders(orderNo, contactName, contactPhone, status));
        }

        // 按创建时间排序（处理null值）
        allOrders.sort((a, b) -> {
            if (a.getCreateTime() == null && b.getCreateTime() == null) {
                return 0;
            }
            if (a.getCreateTime() == null) {
                return 1;
            }
            if (b.getCreateTime() == null) {
                return -1;
            }
            return b.getCreateTime().compareTo(a.getCreateTime());
        });

        // 手动分页
        int total = allOrders.size();
        int start = (currentPage - 1) * size;
        int end = Math.min(start + size, total);

        List<UnifiedOrderDTO> pageRecords = start < total ? allOrders.subList(start, end) : new ArrayList<>();

        Page<UnifiedOrderDTO> page = new Page<>(currentPage, size);
        page.setRecords(pageRecords);
        page.setTotal(total);
        page.setPages((total + size - 1) / size);

        return page;
    }

    /**
     * 获取门票订单
     */
    private List<UnifiedOrderDTO> getTicketOrders(String orderNo, String contactName, String contactPhone, Integer status) {
        List<UnifiedOrderDTO> result = new ArrayList<>();

        LambdaQueryWrapper<TicketOrder> queryWrapper = new LambdaQueryWrapper<>();

        if (orderNo != null && !orderNo.isEmpty()) {
            queryWrapper.like(TicketOrder::getOrderNo, orderNo);
        }
        if (contactName != null && !contactName.isEmpty()) {
            queryWrapper.like(TicketOrder::getVisitorName, contactName);
        }
        if (contactPhone != null && !contactPhone.isEmpty()) {
            queryWrapper.like(TicketOrder::getVisitorPhone, contactPhone);
        }
        if (status != null) {
            queryWrapper.eq(TicketOrder::getStatus, status);
        }

        // 过滤掉create_time为null的记录
        queryWrapper.isNotNull(TicketOrder::getCreateTime);
        queryWrapper.orderByDesc(TicketOrder::getCreateTime);
        List<TicketOrder> orders = ticketOrderMapper.selectList(queryWrapper);

        for (TicketOrder order : orders) {
            UnifiedOrderDTO dto = new UnifiedOrderDTO();
            dto.setId(order.getId());
            dto.setOrderNo(order.getOrderNo());
            dto.setOrderType("TICKET");
            dto.setOrderTypeName("景点门票");
            dto.setUserId(order.getUserId());
            dto.setItemId(order.getTicketId());
            dto.setContactName(order.getVisitorName());
            dto.setContactPhone(order.getVisitorPhone());
            dto.setQuantity(order.getQuantity());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setStatus(order.getStatus());
            dto.setStatusText(getTicketStatusText(order.getStatus()));
            dto.setPaymentMethod(order.getPaymentMethod());
            dto.setPaymentMethodText(getPaymentMethodText(order.getPaymentMethod()));
            dto.setCreateTime(order.getCreateTime());
            dto.setPaymentTime(order.getPaymentTime());
            dto.setUpdateTime(order.getUpdateTime());

            // 设置使用日期
            if (order.getVisitDate() != null) {
                dto.setUseDate(order.getVisitDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }

            // 填充关联信息
            User user = userMapper.selectById(order.getUserId());
            if (user != null) {
                dto.setUsername(user.getUsername());
            }

            Ticket ticket = ticketMapper.selectById(order.getTicketId());
            if (ticket != null) {
                dto.setItemName(ticket.getTicketName());
            }

            result.add(dto);
        }

        return result;
    }

    /**
     * 获取餐饮订单
     */
    private List<UnifiedOrderDTO> getRestaurantOrders(String orderNo, String contactName, String contactPhone, Integer status) {
        List<UnifiedOrderDTO> result = new ArrayList<>();

        LambdaQueryWrapper<RestaurantOrder> queryWrapper = new LambdaQueryWrapper<>();

        if (orderNo != null && !orderNo.isEmpty()) {
            queryWrapper.like(RestaurantOrder::getOrderNo, orderNo);
        }
        if (contactName != null && !contactName.isEmpty()) {
            queryWrapper.like(RestaurantOrder::getContactName, contactName);
        }
        if (contactPhone != null && !contactPhone.isEmpty()) {
            queryWrapper.like(RestaurantOrder::getContactPhone, contactPhone);
        }
        if (status != null) {
            String statusStr = convertStatusToString(status);
            if (statusStr != null) {
                queryWrapper.eq(RestaurantOrder::getStatus, statusStr);
            }
        }

        // 过滤掉create_time为null的记录
        queryWrapper.isNotNull(RestaurantOrder::getCreateTime);
        queryWrapper.orderByDesc(RestaurantOrder::getCreateTime);
        List<RestaurantOrder> orders = restaurantOrderMapper.selectList(queryWrapper);

        for (RestaurantOrder order : orders) {
            UnifiedOrderDTO dto = new UnifiedOrderDTO();
            dto.setId(order.getId().longValue());
            dto.setOrderNo(order.getOrderNo());
            dto.setOrderType("RESTAURANT");
            dto.setOrderTypeName("餐饮预订");
            dto.setUserId(order.getUserId().longValue());
            dto.setItemId(order.getRestaurantId().longValue());
            dto.setContactName(order.getContactName());
            dto.setContactPhone(order.getContactPhone());
            dto.setQuantity(order.getGuestCount());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setStatus(convertStatusToInt(order.getStatus()));
            dto.setStatusText(getRestaurantStatusText(order.getStatus()));
            dto.setPaymentMethod(order.getPaymentMethod());
            dto.setPaymentMethodText(getPaymentMethodText(order.getPaymentMethod()));
            dto.setCreateTime(order.getCreateTime());
            dto.setPaymentTime(order.getPayTime());
            dto.setUpdateTime(order.getUpdateTime());

            // 设置使用日期
            if (order.getReservationDate() != null) {
                dto.setUseDate(order.getReservationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }

            // 填充关联信息
            User user = userMapper.selectById(order.getUserId());
            if (user != null) {
                dto.setUsername(user.getUsername());
            }

            Restaurant restaurant = restaurantMapper.selectById(order.getRestaurantId());
            if (restaurant != null) {
                dto.setItemName(restaurant.getName());
            }

            result.add(dto);
        }

        return result;
    }

    /**
     * 获取酒店订单
     */
    private List<UnifiedOrderDTO> getAccommodationOrders(String orderNo, String contactName, String contactPhone, Integer status) {
        List<UnifiedOrderDTO> result = new ArrayList<>();

        LambdaQueryWrapper<AccommodationOrder> queryWrapper = new LambdaQueryWrapper<>();

        if (orderNo != null && !orderNo.isEmpty()) {
            queryWrapper.like(AccommodationOrder::getOrderNo, orderNo);
        }
        if (contactName != null && !contactName.isEmpty()) {
            queryWrapper.like(AccommodationOrder::getContactName, contactName);
        }
        if (contactPhone != null && !contactPhone.isEmpty()) {
            queryWrapper.like(AccommodationOrder::getContactPhone, contactPhone);
        }
        if (status != null) {
            String statusStr = convertStatusToString(status);
            if (statusStr != null) {
                queryWrapper.eq(AccommodationOrder::getStatus, statusStr);
            }
        }

        // 过滤掉create_time为null的记录
        queryWrapper.isNotNull(AccommodationOrder::getCreateTime);
        queryWrapper.orderByDesc(AccommodationOrder::getCreateTime);
        List<AccommodationOrder> orders = accommodationOrderMapper.selectList(queryWrapper);

        for (AccommodationOrder order : orders) {
            UnifiedOrderDTO dto = new UnifiedOrderDTO();
            dto.setId(order.getId().longValue());
            dto.setOrderNo(order.getOrderNo());
            dto.setOrderType("ACCOMMODATION");
            dto.setOrderTypeName("酒店预订");
            dto.setUserId(order.getUserId().longValue());
            dto.setItemId(order.getAccommodationId().longValue());
            dto.setContactName(order.getContactName());
            dto.setContactPhone(order.getContactPhone());
            dto.setQuantity(order.getGuestCount());
            dto.setTotalAmount(BigDecimal.valueOf(order.getTotalAmount()));
            dto.setStatus(convertStatusToInt(order.getStatus()));
            dto.setStatusText(getAccommodationStatusText(order.getStatus()));
            dto.setPaymentMethod(order.getPaymentMethod());
            dto.setPaymentMethodText(getPaymentMethodText(order.getPaymentMethod()));
            dto.setCreateTime(order.getCreateTime());
            dto.setPaymentTime(order.getPayTime());
            dto.setUpdateTime(order.getUpdateTime());

            // 设置使用日期
            if (order.getCheckInDate() != null && order.getCheckOutDate() != null) {
                String checkIn = order.getCheckInDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String checkOut = order.getCheckOutDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                dto.setUseDate(checkIn + " 至 " + checkOut);
            }

            // 填充关联信息
            User user = userMapper.selectById(order.getUserId());
            if (user != null) {
                dto.setUsername(user.getUsername());
            }

            Accommodation accommodation = accommodationMapper.selectById(order.getAccommodationId());
            if (accommodation != null) {
                dto.setItemName(accommodation.getName());
            }

            result.add(dto);
        }

        return result;
    }

    @Override
    public UnifiedOrderDTO getOrderDetail(String orderType, Long id) {
        switch (orderType) {
            case "TICKET":
                return getTicketOrderDetail(id);
            case "RESTAURANT":
                return getRestaurantOrderDetail(id);
            case "ACCOMMODATION":
                return getAccommodationOrderDetail(id);
            default:
                return null;
        }
    }

    private UnifiedOrderDTO getTicketOrderDetail(Long id) {
        TicketOrder order = ticketOrderMapper.selectById(id);
        if (order == null) return null;

        UnifiedOrderDTO dto = new UnifiedOrderDTO();
        dto.setId(order.getId());
        dto.setOrderNo(order.getOrderNo());
        dto.setOrderType("TICKET");
        dto.setOrderTypeName("景点门票");
        dto.setUserId(order.getUserId());
        dto.setItemId(order.getTicketId());
        dto.setContactName(order.getVisitorName());
        dto.setContactPhone(order.getVisitorPhone());
        dto.setQuantity(order.getQuantity());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setStatusText(getTicketStatusText(order.getStatus()));
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentMethodText(getPaymentMethodText(order.getPaymentMethod()));
        dto.setCreateTime(order.getCreateTime());
        dto.setPaymentTime(order.getPaymentTime());
        dto.setUpdateTime(order.getUpdateTime());

        if (order.getVisitDate() != null) {
            dto.setUseDate(order.getVisitDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        User user = userMapper.selectById(order.getUserId());
        if (user != null) {
            dto.setUsername(user.getUsername());
        }

        Ticket ticket = ticketMapper.selectById(order.getTicketId());
        if (ticket != null) {
            dto.setItemName(ticket.getTicketName());
        }

        return dto;
    }

    private UnifiedOrderDTO getRestaurantOrderDetail(Long id) {
        RestaurantOrder order = restaurantOrderMapper.selectById(id.intValue());
        if (order == null) return null;

        UnifiedOrderDTO dto = new UnifiedOrderDTO();
        dto.setId(order.getId().longValue());
        dto.setOrderNo(order.getOrderNo());
        dto.setOrderType("RESTAURANT");
        dto.setOrderTypeName("餐饮预订");
        dto.setUserId(order.getUserId().longValue());
        dto.setItemId(order.getRestaurantId().longValue());
        dto.setContactName(order.getContactName());
        dto.setContactPhone(order.getContactPhone());
        dto.setQuantity(order.getGuestCount());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(convertStatusToInt(order.getStatus()));
        dto.setStatusText(getRestaurantStatusText(order.getStatus()));
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentMethodText(getPaymentMethodText(order.getPaymentMethod()));
        dto.setCreateTime(order.getCreateTime());
        dto.setPaymentTime(order.getPayTime());
        dto.setUpdateTime(order.getUpdateTime());

        if (order.getReservationDate() != null) {
            dto.setUseDate(order.getReservationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }

        User user = userMapper.selectById(order.getUserId());
        if (user != null) {
            dto.setUsername(user.getUsername());
        }

        Restaurant restaurant = restaurantMapper.selectById(order.getRestaurantId());
        if (restaurant != null) {
            dto.setItemName(restaurant.getName());
        }

        return dto;
    }

    private UnifiedOrderDTO getAccommodationOrderDetail(Long id) {
        AccommodationOrder order = accommodationOrderMapper.selectById(id.intValue());
        if (order == null) return null;

        UnifiedOrderDTO dto = new UnifiedOrderDTO();
        dto.setId(order.getId().longValue());
        dto.setOrderNo(order.getOrderNo());
        dto.setOrderType("ACCOMMODATION");
        dto.setOrderTypeName("酒店预订");
        dto.setUserId(order.getUserId().longValue());
        dto.setItemId(order.getAccommodationId().longValue());
        dto.setContactName(order.getContactName());
        dto.setContactPhone(order.getContactPhone());
        dto.setQuantity(order.getGuestCount());
        dto.setTotalAmount(BigDecimal.valueOf(order.getTotalAmount()));
        dto.setStatus(convertStatusToInt(order.getStatus()));
        dto.setStatusText(getAccommodationStatusText(order.getStatus()));
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentMethodText(getPaymentMethodText(order.getPaymentMethod()));
        dto.setCreateTime(order.getCreateTime());
        dto.setPaymentTime(order.getPayTime());
        dto.setUpdateTime(order.getUpdateTime());

        if (order.getCheckInDate() != null && order.getCheckOutDate() != null) {
            String checkIn = order.getCheckInDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String checkOut = order.getCheckOutDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            dto.setUseDate(checkIn + " 至 " + checkOut);
        }

        User user = userMapper.selectById(order.getUserId());
        if (user != null) {
            dto.setUsername(user.getUsername());
        }

        Accommodation accommodation = accommodationMapper.selectById(order.getAccommodationId());
        if (accommodation != null) {
            dto.setItemName(accommodation.getName());
        }

        return dto;
    }

    @Override
    @Transactional
    public boolean refundOrder(String orderType, Long id) {
        try {
            switch (orderType) {
                case "TICKET":
                    TicketOrder ticketOrder = ticketOrderMapper.selectById(id);
                    if (ticketOrder == null || ticketOrder.getStatus() != 1) {
                        return false;
                    }
                    ticketOrder.setStatus(3);
                    ticketOrderMapper.updateById(ticketOrder);
                    return true;
                case "RESTAURANT":
                    RestaurantOrder restaurantOrder = restaurantOrderMapper.selectById(id.intValue());
                    if (restaurantOrder == null || !"PAID".equals(restaurantOrder.getStatus())) {
                        return false;
                    }
                    restaurantOrder.setStatus("REFUNDED");
                    restaurantOrderMapper.updateById(restaurantOrder);
                    return true;
                case "ACCOMMODATION":
                    AccommodationOrder accommodationOrder = accommodationOrderMapper.selectById(id.intValue());
                    if (accommodationOrder == null || !"PAID".equals(accommodationOrder.getStatus())) {
                        return false;
                    }
                    accommodationOrder.setStatus("REFUNDED");
                    accommodationOrderMapper.updateById(accommodationOrder);
                    return true;
                default:
                    return false;
            }
        } catch (Exception e) {
            LOGGER.error("退款订单失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean completeOrder(String orderType, Long id) {
        try {
            switch (orderType) {
                case "TICKET":
                    TicketOrder ticketOrder = ticketOrderMapper.selectById(id);
                    if (ticketOrder == null || ticketOrder.getStatus() != 1) {
                        return false;
                    }
                    ticketOrder.setStatus(4);
                    ticketOrderMapper.updateById(ticketOrder);
                    return true;
                case "RESTAURANT":
                    RestaurantOrder restaurantOrder = restaurantOrderMapper.selectById(id.intValue());
                    if (restaurantOrder == null || !"PAID".equals(restaurantOrder.getStatus())) {
                        return false;
                    }
                    restaurantOrder.setStatus("COMPLETED");
                    restaurantOrder.setCompleteTime(java.time.LocalDateTime.now());
                    restaurantOrderMapper.updateById(restaurantOrder);
                    return true;
                case "ACCOMMODATION":
                    AccommodationOrder accommodationOrder = accommodationOrderMapper.selectById(id.intValue());
                    if (accommodationOrder == null || !"PAID".equals(accommodationOrder.getStatus())) {
                        return false;
                    }
                    accommodationOrder.setStatus("COMPLETED");
                    accommodationOrder.setCompleteTime(java.time.LocalDateTime.now());
                    accommodationOrderMapper.updateById(accommodationOrder);
                    return true;
                default:
                    return false;
            }
        } catch (Exception e) {
            LOGGER.error("完成订单失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteOrder(String orderType, Long id) {
        try {
            switch (orderType) {
                case "TICKET":
                    ticketOrderMapper.deleteById(id);
                    return true;
                case "RESTAURANT":
                    restaurantOrderMapper.deleteById(id.intValue());
                    return true;
                case "ACCOMMODATION":
                    accommodationOrderMapper.deleteById(id.intValue());
                    return true;
                default:
                    return false;
            }
        } catch (Exception e) {
            LOGGER.error("删除订单失败", e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getOrderStats() {
        Map<String, Object> stats = new HashMap<>();

        // 统计门票订单（不包括已完成的）
        long ticketTotal = ticketOrderMapper.selectCount(
                new LambdaQueryWrapper<TicketOrder>().ne(TicketOrder::getStatus, 4));
        long ticketPending = ticketOrderMapper.selectCount(new LambdaQueryWrapper<TicketOrder>().eq(TicketOrder::getStatus, 0));
        long ticketPaid = ticketOrderMapper.selectCount(new LambdaQueryWrapper<TicketOrder>().eq(TicketOrder::getStatus, 1));
        long ticketCancelled = ticketOrderMapper.selectCount(new LambdaQueryWrapper<TicketOrder>().eq(TicketOrder::getStatus, 2));
        long ticketRefunded = ticketOrderMapper.selectCount(new LambdaQueryWrapper<TicketOrder>().eq(TicketOrder::getStatus, 3));

        // 统计餐饮订单（不包括已完成的）
        long restaurantTotal = restaurantOrderMapper.selectCount(
                new LambdaQueryWrapper<RestaurantOrder>().ne(RestaurantOrder::getStatus, "COMPLETED"));
        long restaurantPending = restaurantOrderMapper.selectCount(new LambdaQueryWrapper<RestaurantOrder>().eq(RestaurantOrder::getStatus, "PENDING"));
        long restaurantPaid = restaurantOrderMapper.selectCount(new LambdaQueryWrapper<RestaurantOrder>().eq(RestaurantOrder::getStatus, "PAID"));
        long restaurantCancelled = restaurantOrderMapper.selectCount(new LambdaQueryWrapper<RestaurantOrder>().eq(RestaurantOrder::getStatus, "CANCELLED"));
        long restaurantRefunded = restaurantOrderMapper.selectCount(new LambdaQueryWrapper<RestaurantOrder>().eq(RestaurantOrder::getStatus, "REFUNDED"));

        // 统计酒店订单（不包括已完成的）
        long accommodationTotal = accommodationOrderMapper.selectCount(
                new LambdaQueryWrapper<AccommodationOrder>().ne(AccommodationOrder::getStatus, "COMPLETED"));
        long accommodationPending = accommodationOrderMapper.selectCount(new LambdaQueryWrapper<AccommodationOrder>().eq(AccommodationOrder::getStatus, "PENDING"));
        long accommodationPaid = accommodationOrderMapper.selectCount(new LambdaQueryWrapper<AccommodationOrder>().eq(AccommodationOrder::getStatus, "PAID"));
        long accommodationCancelled = accommodationOrderMapper.selectCount(new LambdaQueryWrapper<AccommodationOrder>().eq(AccommodationOrder::getStatus, "CANCELLED"));
        long accommodationRefunded = accommodationOrderMapper.selectCount(new LambdaQueryWrapper<AccommodationOrder>().eq(AccommodationOrder::getStatus, "REFUNDED"));

        stats.put("ticket", Map.of(
                "total", ticketTotal,
                "pending", ticketPending,
                "paid", ticketPaid,
                "cancelled", ticketCancelled,
                "refunded", ticketRefunded
        ));

        stats.put("restaurant", Map.of(
                "total", restaurantTotal,
                "pending", restaurantPending,
                "paid", restaurantPaid,
                "cancelled", restaurantCancelled,
                "refunded", restaurantRefunded
        ));

        stats.put("accommodation", Map.of(
                "total", accommodationTotal,
                "pending", accommodationPending,
                "paid", accommodationPaid,
                "cancelled", accommodationCancelled,
                "refunded", accommodationRefunded
        ));

        stats.put("total", ticketTotal + restaurantTotal + accommodationTotal);

        return stats;
    }

    // 辅助方法
    private String getTicketStatusText(Integer status) {
        switch (status) {
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "已取消";
            case 3: return "已退款";
            case 4: return "已完成";
            default: return "未知";
        }
    }

    private String getRestaurantStatusText(String status) {
        switch (status) {
            case "PENDING": return "待支付";
            case "PAID": return "已支付";
            case "CANCELLED": return "已取消";
            case "COMPLETED": return "已完成";
            case "REFUNDED": return "已退款";
            default: return "未知";
        }
    }

    private String getAccommodationStatusText(String status) {
        switch (status) {
            case "PENDING": return "待支付";
            case "PAID": return "已支付";
            case "CANCELLED": return "已取消";
            case "COMPLETED": return "已完成";
            case "REFUNDED": return "已退款";
            default: return "未知";
        }
    }

    private String getPaymentMethodText(String method) {
        if (method == null) return "-";
        switch (method) {
            case "WECHAT": return "微信支付";
            case "ALIPAY": return "支付宝";
            case "BANK_CARD": return "银行卡";
            default: return method;
        }
    }

    private Integer convertStatusToInt(String status) {
        switch (status) {
            case "PENDING": return 0;
            case "PAID": return 1;
            case "CANCELLED": return 2;
            case "REFUNDED": return 3;
            case "COMPLETED": return 4;
            default: return -1;
        }
    }

    private String convertStatusToString(Integer status) {
        switch (status) {
            case 0: return "PENDING";
            case 1: return "PAID";
            case 2: return "CANCELLED";
            case 3: return "REFUNDED";
            case 4: return "COMPLETED";
            default: return null;
        }
    }
}
