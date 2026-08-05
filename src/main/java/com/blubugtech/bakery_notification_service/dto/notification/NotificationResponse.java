package com.blubugtech.bakery_notification_service.dto.notification;

import com.blubugtech.bakery_notification_service.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private UUID id;
    private UUID userId;
    private String recipientEmail;
    private String recipientName;
    @Builder.Default
    private Notification.NotificationStatus status = Notification.NotificationStatus.PENDING;
    private String title;
    private String content;
    private String emailMessageId;
    private String errorMessage;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    private LocalDateTime sentAt;

}
