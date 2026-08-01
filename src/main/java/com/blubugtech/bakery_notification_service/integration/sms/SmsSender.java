package com.blubugtech.bakery_notification_service.integration.sms;

import com.blubugtech.bakery_notification_service.model.NotificationResult;

public interface SmsSender {
    NotificationResult sendSms(String recipientPhone, String content, String tag);
}
