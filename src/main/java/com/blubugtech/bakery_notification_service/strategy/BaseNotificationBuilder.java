package com.blubugtech.bakery_notification_service.strategy;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;

import java.util.HashMap;

public abstract class BaseNotificationBuilder<T> implements NotificationBuilder<T> {

    @Override
    public SendNotificationRequest build(T payload) {
        SendNotificationRequest request = new SendNotificationRequest();
        request.setParams(new HashMap<>());
        
        // Template Method: let subclasses extract basic info
        request.setRecipientEmail(extractEmail(payload));
        request.setRecipientName(extractName(payload));
        request.setUserId(extractUserId(payload));
        
        // Template Method: let subclasses apply specific template logic
        boolean isSupported = applySpecifics(payload, request);
        
        if (!isSupported) {
            return null; // Return null if action/status doesn't map to a template
        }
        
        return request;
    }

    protected abstract String extractEmail(T payload);
    protected abstract String extractName(T payload);
    protected abstract java.util.UUID extractUserId(T payload);
    
    // Returns true if successfully mapped, false otherwise
    protected abstract boolean applySpecifics(T payload, SendNotificationRequest request);
}
