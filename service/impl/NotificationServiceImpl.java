package org.example.springboot.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.example.springboot.entity.Notification;
import org.example.springboot.mapper.NotificationMapper;
import org.example.springboot.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {
    
    @Override
    public Page<Notification> getNotificationPage(Integer currentPage, Integer size) {
        Page<Notification> page = new Page<>(currentPage, size);
        return baseMapper.selectPage(page, null);
    }
    
    @Override
    public List<Notification> getLatestNotifications(Integer limit) {
        return baseMapper.selectList(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getStatus, 1)
                        .orderByDesc(Notification::getCreateTime)
                        .last("limit " + limit)
        );
    }
    
    @Override
    public boolean updateStatus(Long id, Integer status) {
        Notification notification = new Notification();
        notification.setId(id);
        notification.setStatus(status);
        return updateById(notification);
    }
    
    @Override
    public void incrementViewCount(Long id) {
        baseMapper.update(null, 
                new LambdaUpdateWrapper<Notification>()
                        .setSql("view_count = view_count + 1").eq(Notification::getId, id)
        );
    }
}