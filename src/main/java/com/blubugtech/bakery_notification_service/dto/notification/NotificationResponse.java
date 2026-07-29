package com.blubugtech.bakery_notification_service.dto.notification;

import com.blubugtech.bakery_notification_service.entity.Notification;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
public class NotificationResponse {
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

}
