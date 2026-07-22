package com.blubugtech.bakery_notification_service.strategy;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationFactory {

    private final List<NotificationBuilder<?>> builders;

    public NotificationFactory(List<NotificationBuilder<?>> builders) {
        this.builders = builders;
    }

    @SuppressWarnings("unchecked")
    public <T> SendNotificationRequest buildRequest(T payload) {
        for (NotificationBuilder<?> builder : builders) {
            if (builder.supports(payload.getClass())) {
                return ((NotificationBuilder<T>) builder).build(payload);
            }
        }
        throw new IllegalArgumentException("No NotificationBuilder found for payload type: " + payload.getClass().getSimpleName());
    }
}
