package com.blubugtech.bakery_notification_service.kafka.consumer;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.service.NotificationService;
import com.blubugtech.bakery_notification_service.strategy.UserNotificationBuilder;
import org.blubakery.bakery_common_libs.event.UserEvent;
import org.blubakery.bakery_common_libs.contract.messaging.UserPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.blubakery.bakery_common_libs.constants.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);

    private final NotificationService notificationService;
    private final com.blubugtech.bakery_notification_service.strategy.NotificationFactory notificationFactory;

    public UserEventConsumer(NotificationService notificationService, com.blubugtech.bakery_notification_service.strategy.NotificationFactory notificationFactory) {
        this.notificationService = notificationService;
        this.notificationFactory = notificationFactory;
    }

    @KafkaListener(topics = KafkaTopics.USER_TOPIC, groupId = "notification-group")
    public void consume(UserEvent event) {
        UserPayload payload = event.getPayload();
        logger.info("Received UserEvent for User ID: {} with action: {}", payload.getUserId(), payload.getAction());

        try {
            SendNotificationRequest request = notificationFactory.buildRequest(payload);
            if (request != null && request.getTemplateId() != null) {
                notificationService.sendNotification(request);
                logger.info("Notification sent for user action: {} (User ID: {})", payload.getAction(), payload.getUserId());
            } else {
                logger.debug("No template configured or supported for user action: {}", payload.getAction());
            }
        } catch (Exception e) {
            logger.error("Error processing UserEvent for user: {}", payload.getUserId(), e);
        }
    }
}
