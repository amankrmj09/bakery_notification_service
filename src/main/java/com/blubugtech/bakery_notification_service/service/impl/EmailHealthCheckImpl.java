package com.blubugtech.bakery_notification_service.service.impl;

import com.blubugtech.bakery_notification_service.service.EmailHealthCheck;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailHealthCheckImpl implements EmailHealthCheck {

    @Override
    public boolean testEmailConnection() {
        return true;
    }

    @Override
    public Map<String, Object> getEmailServiceHealth() {
        return Map.of("status", "UP", "enabled", true, "connectivity", true);
    }
}
