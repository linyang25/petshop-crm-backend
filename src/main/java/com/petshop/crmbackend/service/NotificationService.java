package com.petshop.crmbackend.service;

import com.petshop.crmbackend.entity.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> findByUserId(String userId);
    void markAsRead(String userId, Long notificationId);
    void markAllAsRead(String userId);
    Notification createNotification(Notification notif);
    List<Notification> findByUserIdAndReadFalse(String userId);


}