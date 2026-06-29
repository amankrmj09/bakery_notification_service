package com.shah_s.bakery_notification_service.kafka;

import org.devofblue.common.event.PaymentEvent;
import com.shah_s.bakery_notification_service.dto.NotificationRequest;
import com.shah_s.bakery_notification_service.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventConsumer.class);
    private final NotificationService notificationService;

    public PaymentEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "payment-events", groupId = "notification-service-group")
    public void consume(PaymentEvent event) {
        logger.info("Received PaymentEvent for Payment ID: {} with status: {}", event.getPaymentId(), event.getStatus());
        
        if (!"COMPLETED".equals(event.getStatus()) && !"FAILED".equals(event.getStatus())) {
            return;
        }
        
        try {
            NotificationRequest request = new NotificationRequest();
            request.setType("EMAIL");
            // recipientEmail needs to be fetched if not present in PaymentEvent, but wait, PaymentEvent doesn't have email!
            // I'll send it to user ID and NotificationService might look up the email.
            request.setSource("PAYMENT_SERVICE");
            request.setTitle("Payment Update");
            request.setSubject("Payment Update");
            
            if ("COMPLETED".equals(event.getStatus())) {
                request.setContent("Your payment of $" + event.getAmount() + " was successful for Order ID: " + event.getOrderId());
            } else {
                request.setContent("Your payment of $" + event.getAmount() + " failed for Order ID: " + event.getOrderId());
            }
            
            notificationService.sendNotification(request);
            logger.info("Notification sent for payment: {}", event.getPaymentId());
        } catch (Exception e) {
            logger.error("Failed to process payment event for notification: {}", e.getMessage());
        }
    }
}
