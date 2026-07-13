package com.shah_s.bakery_notification_service.kafka;

import org.devofblue.common.event.OrderEvent;
import com.shah_s.bakery_notification_service.config.BrevoTemplateProperties;
import com.shah_s.bakery_notification_service.dto.SendNotificationRequestDto;
import com.shah_s.bakery_notification_service.entity.Notification;
import com.shah_s.bakery_notification_service.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);
    private final NotificationService notificationService;
    private final BrevoTemplateProperties templateProperties;

    public OrderEventConsumer(NotificationService notificationService, BrevoTemplateProperties templateProperties) {
        this.notificationService = notificationService;
        this.templateProperties = templateProperties;
    }

    @KafkaListener(topics = "order-events", groupId = "notification-service-group")
    public void consume(OrderEvent event) {
        logger.info("Received OrderEvent for Order ID: {} with status: {}", event.getOrderId(), event.getStatus());
        
        try {
            SendNotificationRequestDto request = new SendNotificationRequestDto();
            request.setRecipientEmail(event.getCustomerEmail());
            request.setRecipientName("Customer");
            request.setUserId(event.getUserId());
            
            // Do not build raw string content. Pass variables in params.
            request.getParams().put("orderId", event.getOrderId());
            request.getParams().put("orderNumber", event.getOrderNumber());
            request.getParams().put("totalAmount", event.getTotalAmount());
            request.getParams().put("status", event.getStatus());
            
            if ("PENDING".equals(event.getStatus())) {
                request.setTitle("Order Confirmation: " + event.getOrderNumber());
                request.setTemplateId(templateProperties.getOrder());
            } else {
                request.setTitle("Order Status Update: " + event.getOrderNumber());
                request.setTemplateId(templateProperties.getTransaction()); 
                // We'll use transaction for status update
            }
            
            notificationService.sendNotification(request);
            logger.info("Notification sent for order: {}", event.getOrderNumber());
        } catch (Exception e) {
            logger.error("Failed to process order event for notification: {}", e.getMessage());
        }
    }
}
