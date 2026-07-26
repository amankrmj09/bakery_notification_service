package com.blubugtech.bakery_notification_service.model;

import lombok.Builder;
import lombok.Data;
import com.blubugtech.bakery_notification_service.enums.NotificationChannel;
import java.util.Map;

@Data
@Builder
public class NotificationRequest {
    private String recipient;
    private String recipientPhone;
    private String title;
    private String body;
    private String smsContent;
    private String smsTag;
    private java.util.Set<NotificationChannel> channels;
    private String templateName;
    private Map<String, Object> data;
}
