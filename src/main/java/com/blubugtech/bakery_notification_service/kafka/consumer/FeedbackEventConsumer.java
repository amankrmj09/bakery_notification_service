package com.blubugtech.bakery_notification_service.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.service.NotificationService;
import com.blubugtech.bakery_notification_service.strategy.FeedbackNotificationBuilder;
import org.blubakery.common.messaging.event.FeedbackEvent;
import org.blubakery.common.messaging.contract.messaging.FeedbackPayload;
import org.blubakery.common.messaging.constants.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FeedbackEventConsumer {

    private final NotificationService notificationService;
    private final com.blubugtech.bakery_notification_service.strategy.NotificationFactory notificationFactory;

    public FeedbackEventConsumer(NotificationService notificationService, com.blubugtech.bakery_notification_service.strategy.NotificationFactory notificationFactory) {
        this.notificationService = notificationService;
        this.notificationFactory = notificationFactory;
    }

    @KafkaListener(topics = KafkaTopics.FEEDBACK_TOPIC, groupId = "notification-group")
    public void consume(FeedbackEvent event) {
        FeedbackPayload payload = event.getPayload();
        log.info("Received FeedbackEvent for User ID: {} of type: {}", payload.getUserId(), payload.getType());

        try {
            SendNotificationRequest request = notificationFactory.buildRequest(payload);
            if (request != null && request.getTemplateId() != null) {
                notificationService.sendNotification(request);
                log.info("Notification sent for feedback type: {} (User ID: {})", payload.getType(), payload.getUserId());
            } else {
                log.debug("No template configured or supported for feedback type: {}", payload.getType());
            }
        } catch (Exception e) {
            log.error("Error processing FeedbackEvent for user: {}", payload.getUserId(), e);
        }
    }
}
