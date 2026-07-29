package com.blubugtech.bakery_notification_service.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.service.NotificationService;
import com.blubugtech.bakery_notification_service.strategy.PaymentNotificationBuilder;
import org.blubakery.common.messaging.event.PaymentEvent;
import org.blubakery.common.messaging.contract.messaging.PaymentPayload;
import org.blubakery.common.messaging.constants.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentEventConsumer {

    private final NotificationService notificationService;
    private final com.blubugtech.bakery_notification_service.strategy.NotificationFactory notificationFactory;

    public PaymentEventConsumer(NotificationService notificationService, com.blubugtech.bakery_notification_service.strategy.NotificationFactory notificationFactory) {
        this.notificationService = notificationService;
        this.notificationFactory = notificationFactory;
    }

    @KafkaListener(topics = KafkaTopics.PAYMENTS_TOPIC, groupId = "notification-group")
    public void consume(PaymentEvent event) {
        PaymentPayload payload = event.getPayload();
        log.info("Received PaymentEvent for Payment ID: {} with status: {}", payload.getPaymentId(), payload.getStatus());

        try {
            SendNotificationRequest request = notificationFactory.buildRequest(payload);
            if (request != null && request.getTemplateId() != null) {
                notificationService.sendNotification(request);
                log.info("Notification sent for payment status: {} (Payment ID: {})", payload.getStatus(), payload.getPaymentId());
            } else {
                log.debug("No template configured or supported for payment status: {}", payload.getStatus());
            }
        } catch (Exception e) {
            log.error("Error processing PaymentEvent for payment: {}", payload.getPaymentId(), e);
        }
    }
}
