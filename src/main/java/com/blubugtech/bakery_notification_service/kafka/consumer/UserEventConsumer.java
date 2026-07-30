package com.blubugtech.bakery_notification_service.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.service.NotificationService;
import org.blubakery.common.messaging.event.UserEvent;
import org.blubakery.common.messaging.contract.messaging.UserPayload;
import org.blubakery.common.messaging.constants.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class UserEventConsumer {

    private final NotificationService notificationService;
    private final com.blubugtech.bakery_notification_service.strategy.NotificationFactory notificationFactory;

    public UserEventConsumer(NotificationService notificationService, com.blubugtech.bakery_notification_service.strategy.NotificationFactory notificationFactory) {
        this.notificationService = notificationService;
        this.notificationFactory = notificationFactory;
    }

    @KafkaListener(topics = KafkaTopics.USER_TOPIC, groupId = "notification-group")
    public void consume(UserEvent event) {
        UserPayload payload = event.getPayload();
        log.info("Received UserEvent for User ID: {} with action: {}", payload.getUserId(), payload.getAction());

        try {
            List<SendNotificationRequest> requests = notificationFactory.buildRequests(payload);
            for (SendNotificationRequest request : requests) {
                if (request.getTemplateId() != null) {
                    notificationService.sendNotification(request);
                    log.info("Notification sent for user action: {} (User ID: {})", payload.getAction(), payload.getUserId());
                }
            }
        } catch (Exception e) {
            log.error("Error processing UserEvent for user: {}", payload.getUserId(), e);
        }
    }
}
