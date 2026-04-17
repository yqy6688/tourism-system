package org.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.springboot.entity.Notification;

import java.util.List;

public interface NotificationService extends IService<Notification> {
    
    Page<Notification> getNotificationPage(Integer currentPage, Integer size);
    
    List<Notification> getLatestNotifications(Integer limit);
    
    boolean updateStatus(Long id, Integer status);
    
    void incrementViewCount(Long id);
}