package com.petshop.crmbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petshop.crmbackend.entity.Notification;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Notification entity,
 * sent to front-end clients.
 */
public class NotificationDto {

    /** 通知 ID */
    private Long id;

    /** 通知标题 */
    private String title;

    /** 通知内容 */
    private String content;

    /** 是否已读 */
    @JsonProperty("isRead")
    private boolean read;

    /** 创建时间 */
    private LocalDateTime createdAt;

    public NotificationDto() {
    }

    public NotificationDto(Long id, String title, String content, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.read = read;
        this.createdAt = createdAt;
    }

    public static NotificationDto fromEntity(Notification e) {
        return new NotificationDto(
                e.getId(),
                e.getTitle(),
                e.getContent(),
                e.isRead(),
                e.getCreatedAt()
        );
    }

    // ======== Getters & Setters ========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @JsonProperty("isRead")
    public boolean isRead() {
        return read;
    }

    @JsonProperty("isRead")
    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "NotificationDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", read=" + read +
                ", createdAt=" + createdAt +
                '}';
    }
}