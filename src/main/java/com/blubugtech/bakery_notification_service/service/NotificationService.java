package com.blubugtech.bakery_notification_service.service;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.dto.notification.NotificationResponse;

public interface NotificationService {
    NotificationResponse sendNotification(SendNotificationRequest request);
    
    org.blubakery.common.core.dto.RestPageResponse<NotificationResponse> getNotificationsByUser(java.util.UUID userId, org.springframework.data.domain.Pageable pageable);
}
