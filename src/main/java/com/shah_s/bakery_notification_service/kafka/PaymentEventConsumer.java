package com.shah_s.bakery_notification_service.kafka;

import org.devofblue.common.event.PaymentEvent;
import com.shah_s.bakery_notification_service.config.BrevoTemplateProperties;
import com.shah_s.bakery_notification_service.dto.SendNotificationRequestDto;
import com.shah_s.bakery_notification_service.entity.Notification;
import com.shah_s.bakery_notification_service.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventConsumer.class);
    private final NotificationService notificationService;
    private final BrevoTemplateProperties templateProperties;

    public PaymentEventConsumer(NotificationService notificationService, BrevoTemplateProperties templateProperties) {
        this.notificationService = notificationService;
        this.templateProperties = templateProperties;
    }

    @KafkaListener(topics = "${kafka.topic.payment-events}", groupId = "notification-service-group")
    public void consumePaymentEvent(PaymentEvent event) {
        logger.info("Received PaymentEvent for Payment ID: {} with status: {}", event.getPaymentId(), event.getStatus());
        
        if (!"COMPLETED".equals(event.getStatus()) && !"FAILED".equals(event.getStatus())) {
            return;
        }
        
        try {
            SendNotificationRequestDto request = new SendNotificationRequestDto();
            request.setRecipientEmail(event.getCustomerEmail() != null ? event.getCustomerEmail() : "customer@example.com"); // fallback just in case old events don't have it
            request.setTitle("Payment Update");
            
            // Set dynamic parameters
            request.getParams().put("paymentId", event.getPaymentId());
            request.getParams().put("orderId", event.getOrderId());
            request.getParams().put("amount", event.getAmount());
            request.getParams().put("status", event.getStatus());
            
            if ("COMPLETED".equals(event.getStatus())) {
                request.setTemplateId(templateProperties.getPayment());
            } else {
                // If payment failed, we might use transaction or payment template with different param
                request.setTemplateId(templateProperties.getPayment());
            }
            
            notificationService.sendNotification(request);
            logger.info("Notification sent for payment: {}", event.getPaymentId());
        } catch (Exception e) {
            logger.error("Failed to process payment event for notification: {}", e.getMessage());
        }
    }
}
