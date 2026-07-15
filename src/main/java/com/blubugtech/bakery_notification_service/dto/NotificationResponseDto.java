package com.blubugtech.bakery_notification_service.dto;

import com.blubugtech.bakery_notification_service.entity.Notification;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationResponseDto {
    private UUID id;
    private UUID userId;
    private String recipientEmail;
    private String recipientName;
    private Notification.NotificationStatus status;
    private String title;
    private String content;
    private String emailMessageId;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime sentAt;

    public static NotificationResponseDto from(Notification notification) {
        NotificationResponseDto response = new NotificationResponseDto();
        response.id = notification.getId();
        response.userId = notification.getUserId();
        response.recipientEmail = notification.getRecipientEmail();
        response.recipientName = notification.getRecipientName();
        response.status = notification.getStatus();
        response.title = notification.getTitle();
        response.content = notification.getContent();
        response.emailMessageId = notification.getEmailMessageId();
        response.errorMessage = notification.getErrorMessage();
        response.createdAt = notification.getCreatedAt();
        response.updatedAt = notification.getUpdatedAt();
        response.sentAt = notification.getSentAt();
        return response;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public Notification.NotificationStatus getStatus() { return status; }
    public void setStatus(Notification.NotificationStatus status) { this.status = status; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getEmailMessageId() { return emailMessageId; }
    public void setEmailMessageId(String emailMessageId) { this.emailMessageId = emailMessageId; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}
