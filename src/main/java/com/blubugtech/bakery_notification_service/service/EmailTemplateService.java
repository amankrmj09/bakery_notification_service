package com.blubugtech.bakery_notification_service.service;

public interface EmailTemplateService {
    void sendAutoReplyToUser(String userName, String userEmail);
    void sendNotificationToAdmin(String userName, String userEmail, String phone, String instagramId, String message);
}
