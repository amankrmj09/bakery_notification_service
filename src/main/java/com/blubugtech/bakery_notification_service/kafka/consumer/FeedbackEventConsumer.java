package com.blubugtech.bakery_notification_service.kafka.consumer;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.service.NotificationService;
import com.blubugtech.bakery_notification_service.strategy.FeedbackNotificationBuilder;
import com.blubugtech.common.event.FeedbackEvent;
import com.blubugtech.common.contract.messaging.FeedbackPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FeedbackEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackEventConsumer.class);

    private final NotificationService notificationService;
    private final com.blubugtech.bakery_notification_service.strategy.NotificationFactory notificationFactory;

    public FeedbackEventConsumer(NotificationService notificationService, com.blubugtech.bakery_notification_service.strategy.NotificationFactory notificationFactory) {
        this.notificationService = notificationService;
        this.notificationFactory = notificationFactory;
    }

    @KafkaListener(topics = "feedback-events", groupId = "notification-group")
    public void consume(FeedbackEvent event) {
        FeedbackPayload payload = event.getPayload();
        logger.info("Received FeedbackEvent for User ID: {} of type: {}", payload.getUserId(), payload.getType());

        try {
            SendNotificationRequest request = notificationFactory.buildRequest(payload);
            if (request != null && request.getTemplateId() != null) {
                notificationService.sendNotification(request);
                logger.info("Notification sent for feedback type: {} (User ID: {})", payload.getType(), payload.getUserId());
            } else {
                logger.debug("No template configured or supported for feedback type: {}", payload.getType());
            }
        } catch (Exception e) {
            logger.error("Error processing FeedbackEvent for user: {}", payload.getUserId(), e);
        }
    }
}
