package com.blubugtech.bakery_notification_service.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResult {
    private boolean success;
    private String messageId;
    private String errorMessage;
}
