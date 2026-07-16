package com.blubugtech.bakery_notification_service.service.sender;

import com.blubugtech.bakery_notification_service.enums.NotificationChannel;
import com.blubugtech.bakery_notification_service.model.EmailMessage;
import com.blubugtech.bakery_notification_service.model.NotificationResult;

public interface EmailSender {
    NotificationResult sendEmail(EmailMessage message);
    boolean supports(NotificationChannel channel);
}
