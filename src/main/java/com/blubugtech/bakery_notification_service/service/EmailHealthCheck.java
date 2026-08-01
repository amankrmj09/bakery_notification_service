package com.blubugtech.bakery_notification_service.service;

import java.util.Map;

public interface EmailHealthCheck {
    boolean testEmailConnection();
    Map<String, Object> getEmailServiceHealth();
}
