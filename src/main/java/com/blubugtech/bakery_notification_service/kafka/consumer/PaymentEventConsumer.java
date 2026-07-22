package com.blubugtech.bakery_notification_service.kafka.consumer;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.service.NotificationService;
import com.blubugtech.bakery_notification_service.strategy.PaymentNotificationBuilder;
import com.blubugtech.common.event.PaymentEvent;
import com.blubugtech.common.contract.messaging.PaymentPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventConsumer.class);

    private final NotificationService notificationService;
    private final com.blubugtech.bakery_notification_service.strategy.NotificationFactory notificationFactory;

    public PaymentEventConsumer(NotificationService notificationService, com.blubugtech.bakery_notification_service.strategy.NotificationFactory notificationFactory) {
        this.notificationService = notificationService;
        this.notificationFactory = notificationFactory;
    }

    @KafkaListener(topics = "payment-events", groupId = "notification-group")
    public void consume(PaymentEvent event) {
        PaymentPayload payload = event.getPayload();
        logger.info("Received PaymentEvent for Payment ID: {} with status: {}", payload.getPaymentId(), payload.getStatus());

        try {
            SendNotificationRequest request = notificationFactory.buildRequest(payload);
            if (request != null && request.getTemplateId() != null) {
                notificationService.sendNotification(request);
                logger.info("Notification sent for payment status: {} (Payment ID: {})", payload.getStatus(), payload.getPaymentId());
            } else {
                logger.debug("No template configured or supported for payment status: {}", payload.getStatus());
            }
        } catch (Exception e) {
            logger.error("Error processing PaymentEvent for payment: {}", payload.getPaymentId(), e);
        }
    }
}
