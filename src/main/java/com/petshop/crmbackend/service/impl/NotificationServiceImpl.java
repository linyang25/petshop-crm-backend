package com.petshop.crmbackend.service.impl;

import com.petshop.crmbackend.entity.Notification;
import com.petshop.crmbackend.repository.NotificationRepository;
import com.petshop.crmbackend.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repo;

    public NotificationServiceImpl(NotificationRepository repo) {
        this.repo = repo;
    }

    /**
     * 1. 查询指定用户的所有通知，按时间倒序（最新在前）
     */
    @Override
    public List<Notification> findByUserId(String userId) {
        return repo.findByTargetUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 2. 查询指定用户所有未读通知
     */
    @Override
    public List<Notification> findByUserIdAndReadFalse(String userId) {
        return repo.findByTargetUserIdAndReadFalseOrderByCreatedAtDesc(userId);
    }

    /**
     * 3. 标记单条通知为已读
     */
    @Override
    @Transactional
    public void markAsRead(String userId, Long notificationId) {
        repo.findByTargetUserIdAndId(userId, notificationId)
                .ifPresent(n -> {
                    n.setRead(true);       // 假设实体里字段名是 `private boolean read;`
                    repo.save(n);
                });
    }

    /**
     * 4. 标记该用户所有未读通知为已读
     */
    @Override
    @Transactional
    public void markAllAsRead(String userId) {
        // 优先使用批量更新，效率最高
        int updated = repo.markAllReadByTargetUserId(userId);
        if (updated >= 0) {
            return;
        }
        // 否则 fallback：查询再逐条更新
        List<Notification> unread = repo.findByTargetUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        unread.forEach(n -> n.setRead(true));
        repo.saveAll(unread);
    }

    /**
     * 5. 创建一条新通知
     */
    @Override
    @Transactional
    public Notification createNotification(Notification notif) {
        return repo.save(notif);
    }
}
