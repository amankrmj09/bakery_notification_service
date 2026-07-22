package com.blubugtech.bakery_notification_service.strategy;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;

public interface NotificationBuilder<T> {
    SendNotificationRequest build(T payload);
    boolean supports(Class<?> payloadType);
}
