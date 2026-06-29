package com.shah_s.bakery_notification_service.kafka;

import org.devofblue.common.event.OrderEvent;
import com.shah_s.bakery_notification_service.dto.NotificationRequest;
import com.shah_s.bakery_notification_service.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);
    private final NotificationService notificationService;

    public OrderEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "order-events", groupId = "notification-service-group")
    public void consume(OrderEvent event) {
        logger.info("Received OrderEvent for Order ID: {} with status: {}", event.getOrderId(), event.getStatus());
        
        try {
            NotificationRequest request = new NotificationRequest();
            request.setType("EMAIL");
            request.setRecipientEmail(event.getCustomerEmail());
            request.setRecipientName("Customer");
            request.setSource("ORDER_SERVICE");
            request.setUserId(event.getUserId());
            
            if ("PENDING".equals(event.getStatus())) {
                request.setTitle("Order Confirmation: " + event.getOrderNumber());
                request.setSubject("Order Confirmation: " + event.getOrderNumber());
                request.setContent("Your order has been received and is being processed. Total: $" + event.getTotalAmount());
            } else {
                request.setTitle("Order Status Update: " + event.getOrderNumber());
                request.setSubject("Order Status Update: " + event.getOrderNumber());
                request.setContent("Your order status is now: " + event.getStatus());
            }
            
            notificationService.sendNotification(request);
            logger.info("Notification sent for order: {}", event.getOrderNumber());
        } catch (Exception e) {
            logger.error("Failed to process order event for notification: {}", e.getMessage());
        }
    }
}
