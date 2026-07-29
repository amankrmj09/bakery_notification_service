package com.blubugtech.bakery_notification_service.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.service.NotificationService;
import com.blubugtech.bakery_notification_service.strategy.OrderNotificationBuilder;
import org.blubakery.common.messaging.event.OrderEvent;
import org.blubakery.common.messaging.contract.messaging.OrderPayload;
import org.blubakery.common.messaging.constants.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventConsumer {

    private final NotificationService notificationService;
    private final com.blubugtech.bakery_notification_service.strategy.NotificationFactory notificationFactory;

    public OrderEventConsumer(NotificationService notificationService, com.blubugtech.bakery_notification_service.strategy.NotificationFactory notificationFactory) {
        this.notificationService = notificationService;
        this.notificationFactory = notificationFactory;
    }

    @KafkaListener(topics = KafkaTopics.ORDERS_TOPIC, groupId = "notification-group")
    public void consume(OrderEvent event) {
        OrderPayload payload = event.getPayload();
        log.info("Received OrderEvent for Order ID: {} with status: {}", payload.getOrderId(), payload.getStatus());

        try {
            SendNotificationRequest request = notificationFactory.buildRequest(payload);
            if (request != null && request.getTemplateId() != null) {
                notificationService.sendNotification(request);
                log.info("Notification sent for order status: {} (Order ID: {})", payload.getStatus(), payload.getOrderId());
            } else {
                log.debug("No template configured or supported for order status: {}", payload.getStatus());
            }
        } catch (Exception e) {
            log.error("Error processing OrderEvent for order: {}", payload.getOrderId(), e);
        }
    }
}
