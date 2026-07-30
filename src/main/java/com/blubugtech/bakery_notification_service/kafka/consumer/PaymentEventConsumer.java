package com.blubugtech.bakery_notification_service.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.service.NotificationService;
import org.blubakery.common.messaging.event.PaymentEvent;
import org.blubakery.common.messaging.contract.messaging.PaymentPayload;
import org.blubakery.common.messaging.constants.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

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
            List<SendNotificationRequest> requests = notificationFactory.buildRequests(payload);
            for (SendNotificationRequest request : requests) {
                if (request.getTemplateId() != null) {
                    notificationService.sendNotification(request);
                    log.info("Notification sent for payment status: {} (Payment ID: {})", payload.getStatus(), payload.getPaymentId());
                }
            }
        } catch (Exception e) {
            log.error("Error processing PaymentEvent for payment: {}", payload.getPaymentId(), e);
        }
    }
}
