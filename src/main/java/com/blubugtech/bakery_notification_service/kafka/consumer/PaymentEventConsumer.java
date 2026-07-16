package com.blubugtech.bakery_notification_service.kafka.consumer;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.integration.brevo.BrevoTemplateProperties;
import com.blubugtech.bakery_notification_service.service.NotificationService;
import com.blubugtech.common.event.PaymentEvent;
import com.blubugtech.common.contract.messaging.PaymentPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class PaymentEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventConsumer.class);

    private final NotificationService notificationService;
    private final BrevoTemplateProperties templateProperties;

    public PaymentEventConsumer(NotificationService notificationService, BrevoTemplateProperties templateProperties) {
        this.notificationService = notificationService;
        this.templateProperties = templateProperties;
    }

    @KafkaListener(topics = "payment-events", groupId = "notification-group")
    public void consume(PaymentEvent event) {
        PaymentPayload payload = event.getPayload();
        logger.info("Received PaymentEvent for Payment ID: {} with status: {}", payload.getPaymentId(), payload.getStatus());

        try {
            SendNotificationRequest request = new SendNotificationRequest();
            request.setRecipientEmail(payload.getCustomerEmail());
            request.setRecipientName("Customer");
            request.setTitle("Payment Update");
            request.setContent("Your payment " + payload.getPaymentId() + " is " + payload.getStatus());
            request.setUserId(payload.getUserId());
            
            request.setParams(new HashMap<>());
            request.getParams().put("paymentId", payload.getPaymentId());
            request.getParams().put("orderId", payload.getOrderId());
            request.getParams().put("amount", payload.getAmount());
            request.getParams().put("status", payload.getStatus());

            if ("SUCCESS".equals(payload.getStatus())) {
                request.setTemplateId(Long.valueOf(templateProperties.getPayment()));
                notificationService.sendNotification(request);
                logger.info("Notification sent for successful payment: {}", payload.getPaymentId());
            } else {
                notificationService.sendNotification(request);
                logger.info("Notification sent for payment: {}", payload.getPaymentId());
            }

        } catch (Exception e) {
            logger.error("Error processing PaymentEvent for payment: {}", payload.getPaymentId(), e);
        }
    }
}
