package com.petshop.crmbackend.repository;

import com.petshop.crmbackend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 1. 按时间倒序查询该用户所有通知
    List<Notification> findByTargetUserIdOrderByCreatedAtDesc(String targetUserId);

    // 2. 查询单条通知，保证属于该用户
    Optional<Notification> findByTargetUserIdAndId(String userId, Long notificationId);

    // 3. 批量标记为已读，需要 @Modifying + @Query
    @Modifying
    @Query("UPDATE Notification n SET n.read = TRUE WHERE n.targetUserId = :userId AND n.read = FALSE")
    int markAllReadByTargetUserId(@Param("userId") String userId);


    // 4. 查询该用户所有未读通知
    List<Notification> findByTargetUserIdAndReadFalseOrderByCreatedAtDesc(String userId);
}
