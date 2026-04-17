package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Notification;
import org.example.springboot.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "通知公告管理")
@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Resource
    private NotificationService notificationService;

    @Operation(summary = "获取通知公告列表")
    @GetMapping("/page")
    public Result<Page<Notification>> getNotificationPage(
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<Notification> page = notificationService.getNotificationPage(currentPage, size);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("获取通知公告列表失败");
        }
    }

    @Operation(summary = "获取最新通知公告")
    @GetMapping("/latest")
    public Result<List<Notification>> getLatestNotifications(
            @RequestParam(defaultValue = "5") Integer limit) {
        try {
            List<Notification> notifications = notificationService.getLatestNotifications(limit);
            return Result.success(notifications);
        } catch (Exception e) {
            return Result.error("获取最新通知公告失败");
        }
    }

    @Operation(summary = "添加通知公告")
    @PostMapping
    public Result<String> addNotification(@RequestBody Notification notification) {
        try {
            boolean success = notificationService.save(notification);
            if (success) {
                return Result.success("添加成功");
            } else {
                return Result.error("添加失败");
            }
        } catch (Exception e) {
            return Result.error("添加失败");
        }
    }

    @Operation(summary = "编辑通知公告")
    @PutMapping
    public Result<String> updateNotification(@RequestBody Notification notification) {
        try {
            boolean success = notificationService.updateById(notification);
            if (success) {
                return Result.success("编辑成功");
            } else {
                return Result.error("编辑失败");
            }
        } catch (Exception e) {
            return Result.error("编辑失败");
        }
    }

    @Operation(summary = "删除通知公告")
    @DeleteMapping("/{id}")
    public Result<String> deleteNotification(@PathVariable Long id) {
        try {
            boolean success = notificationService.removeById(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除失败");
        }
    }

    @Operation(summary = "修改通知公告状态")
    @PutMapping("/status/{id}")
    public Result<String> updateNotificationStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            boolean success = notificationService.updateStatus(id, status);
            if (success) {
                return Result.success("状态修改成功");
            } else {
                return Result.error("状态修改失败");
            }
        } catch (Exception e) {
            return Result.error("状态修改失败");
        }
    }

    @Operation(summary = "增加通知公告阅读量")
    @PutMapping("/view/{id}")
    public Result<String> incrementViewCount(@PathVariable Long id) {
        try {
            notificationService.incrementViewCount(id);
            return Result.success("阅读量更新成功");
        } catch (Exception e) {
            return Result.error("阅读量更新失败");
        }
    }

    @Operation(summary = "获取通知公告详情")
    @GetMapping("/{id}")
    public Result<Notification> getNotificationById(@PathVariable Long id) {
        try {
            Notification notification = notificationService.getById(id);
            if (notification != null) {
                // 增加阅读量
                notificationService.incrementViewCount(id);
                return Result.success(notification);
            } else {
                return Result.error("通知公告不存在");
            }
        } catch (Exception e) {
            return Result.error("获取通知公告详情失败");
        }
    }
}