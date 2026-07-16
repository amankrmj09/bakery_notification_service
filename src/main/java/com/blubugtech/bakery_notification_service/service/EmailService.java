package com.blubugtech.bakery_notification_service.service;

import com.blubugtech.bakery_notification_service.entity.Notification;
import java.util.Map;

public interface EmailService {
    void sendAutoReplyToUser(String userName, String userEmail);
    void sendNotificationToAdmin(String userName, String userEmail, String phone, String instagramId, String message);
    void sendEmail(Notification notification, Map<String, Object> customParams);
    boolean testEmailConnection();
    Map<String, Object> getEmailServiceHealth();
}
