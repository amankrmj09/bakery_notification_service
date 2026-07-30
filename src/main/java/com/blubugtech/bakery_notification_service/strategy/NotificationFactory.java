package com.blubugtech.bakery_notification_service.strategy;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class NotificationFactory {

    private final List<NotificationBuilder<?>> builders;

    public NotificationFactory(List<NotificationBuilder<?>> builders) {
        this.builders = builders;
    }

    @SuppressWarnings("unchecked")
    public <T> List<SendNotificationRequest> buildRequests(T payload) {
        List<SendNotificationRequest> requests = new java.util.ArrayList<>();
        for (NotificationBuilder<?> builder : builders) {
            if (builder.supports(payload.getClass())) {
                SendNotificationRequest req = ((NotificationBuilder<T>) builder).build(payload);
                if (req != null) {
                    requests.add(req);
                }
            }
        }
        if (requests.isEmpty()) {
            log.debug("No notifications built for payload type: {}", payload.getClass().getSimpleName());
        }
        return requests;
    }
}
