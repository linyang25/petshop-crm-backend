package com.petshop.crmbackend.controller;

import com.petshop.crmbackend.common.ApiResponse;
import com.petshop.crmbackend.dto.NotificationDto;
import com.petshop.crmbackend.entity.Notification;
import com.petshop.crmbackend.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 1. 获取指定 target_user_id 的所有通知
     *    直接传 userId，不走 Spring Security 注入
     */
    @GetMapping
    public ApiResponse<List<NotificationDto>> listNotifications(
            @RequestParam("userId") String userId
    ) {
        List<Notification> all = notificationService.findByUserId(userId);
        List<NotificationDto> dtos = all.stream()
                .map(NotificationDto::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.success("Fetched notifications", dtos);
    }

    /**
     * 2. 标记单条通知为已读，并返回最新列表
     */
    @PostMapping("/{id}/read")
    public ApiResponse<List<NotificationDto>> markAsRead(
            @RequestParam("userId") String userId,
            @PathVariable("id") Long notificationId
    ) {
        notificationService.markAsRead(userId, notificationId);

        List<NotificationDto> dtos = notificationService.findByUserId(userId).stream()
                .map(NotificationDto::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.success("Marked as read", dtos);
    }

    /**
     * 3. 标记所有未读通知为已读，并返回最新列表
     */
    @PostMapping("/read-all")
    public ApiResponse<List<NotificationDto>> markAllAsRead(
            @RequestParam("userId") String userId
    ) {
        notificationService.markAllAsRead(userId);

        List<NotificationDto> dtos = notificationService.findByUserId(userId).stream()
                .map(NotificationDto::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.success("All notifications marked as read", dtos);
    }
}
