package com.blubugtech.bakery_notification_service.integration.email;

import com.blubugtech.bakery_notification_service.model.EmailMessage;
import com.blubugtech.bakery_notification_service.model.NotificationResult;

public interface EmailSender {
    NotificationResult send(EmailMessage message);
}
