package com.blubugtech.bakery_notification_service.kafka.consumer;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.integration.brevo.BrevoTemplateProperties;
import com.blubugtech.bakery_notification_service.service.NotificationService;
import com.blubugtech.common.event.OrderEvent;
import com.blubugtech.common.contract.messaging.OrderPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class OrderEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final NotificationService notificationService;
    private final BrevoTemplateProperties templateProperties;

    public OrderEventConsumer(NotificationService notificationService, BrevoTemplateProperties templateProperties) {
        this.notificationService = notificationService;
        this.templateProperties = templateProperties;
    }

    @KafkaListener(topics = "order-events", groupId = "notification-group")
    public void consume(OrderEvent event) {
        OrderPayload payload = event.getPayload();
        logger.info("Received OrderEvent for Order ID: {} with status: {}", payload.getOrderId(), payload.getStatus());

        try {
            SendNotificationRequest request = new SendNotificationRequest();
            request.setRecipientEmail(payload.getCustomerEmail());
            request.setRecipientName("Customer");
            request.setTitle("Order Notification");
            request.setContent("Your order " + payload.getOrderId() + " status is now " + payload.getStatus());
            request.setUserId(payload.getUserId());
            
            request.setParams(new HashMap<>());
            request.getParams().put("orderId", payload.getOrderId());
            request.getParams().put("status", payload.getStatus());
            request.getParams().put("totalAmount", payload.getTotalAmount());
            request.getParams().put("orderNumber", payload.getOrderNumber());

            if ("CONFIRMED".equals(payload.getStatus())) {
                request.setTemplateId(Long.valueOf(templateProperties.getOrder()));
                notificationService.sendNotification(request);
                logger.info("Notification sent for confirmed order: {}", payload.getOrderId());
            } else {
                notificationService.sendNotification(request);
                logger.info("Notification sent for order: {}", payload.getOrderId());
            }

        } catch (Exception e) {
            logger.error("Error processing OrderEvent for order: {}", payload.getOrderId(), e);
        }
    }
}
