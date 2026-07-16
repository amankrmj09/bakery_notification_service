package com.blubugtech.bakery_notification_service.service.sender;

import com.blubugtech.bakery_notification_service.model.NotificationRequest;
import com.blubugtech.bakery_notification_service.model.NotificationResult;
import com.blubugtech.bakery_notification_service.enums.NotificationChannel;

public interface NotificationSender {
    boolean supports(NotificationChannel channel);
    NotificationResult send(NotificationRequest request);
}
