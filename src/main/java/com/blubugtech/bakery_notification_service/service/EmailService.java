package com.blubugtech.bakery_notification_service.service;

import com.blubugtech.bakery_notification_service.entity.Notification;
import java.util.Map;

public interface EmailService {
    void sendEmail(Notification notification, Map<String, Object> customParams);
}
