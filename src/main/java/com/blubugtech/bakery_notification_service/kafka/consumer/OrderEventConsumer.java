package com.blubugtech.bakery_notification_service.kafka.consumer;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.service.NotificationService;
import com.blubugtech.bakery_notification_service.strategy.OrderNotificationBuilder;
import com.blubugtech.common.event.OrderEvent;
import com.blubugtech.common.contract.messaging.OrderPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final NotificationService notificationService;
    private final com.blubugtech.bakery_notification_service.strategy.NotificationFactory notificationFactory;

    public OrderEventConsumer(NotificationService notificationService, com.blubugtech.bakery_notification_service.strategy.NotificationFactory notificationFactory) {
        this.notificationService = notificationService;
        this.notificationFactory = notificationFactory;
    }

    @KafkaListener(topics = "order-events", groupId = "notification-group")
    public void consume(OrderEvent event) {
        OrderPayload payload = event.getPayload();
        logger.info("Received OrderEvent for Order ID: {} with status: {}", payload.getOrderId(), payload.getStatus());

        try {
            SendNotificationRequest request = notificationFactory.buildRequest(payload);
            if (request != null && request.getTemplateId() != null) {
                notificationService.sendNotification(request);
                logger.info("Notification sent for order status: {} (Order ID: {})", payload.getStatus(), payload.getOrderId());
            } else {
                logger.debug("No template configured or supported for order status: {}", payload.getStatus());
            }
        } catch (Exception e) {
            logger.error("Error processing OrderEvent for order: {}", payload.getOrderId(), e);
        }
    }
}
