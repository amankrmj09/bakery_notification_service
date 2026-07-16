package com.blubugtech.bakery_notification_service.validation;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import org.springframework.stereotype.Component;

@Component
public class NotificationValidator {
    public boolean isValid(SendNotificationRequest request) {
        return request != null && request.getRecipientEmail() != null && !request.getRecipientEmail().isEmpty();
    }
}
