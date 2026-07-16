package com.blubugtech.bakery_notification_service.kafka.consumer;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.integration.brevo.BrevoTemplateProperties;
import com.blubugtech.bakery_notification_service.service.NotificationService;
import com.blubugtech.common.event.UserEvent;
import com.blubugtech.common.contract.messaging.UserPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class UserEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);

    private final NotificationService notificationService;
    private final BrevoTemplateProperties templateProperties;

    public UserEventConsumer(NotificationService notificationService, BrevoTemplateProperties templateProperties) {
        this.notificationService = notificationService;
        this.templateProperties = templateProperties;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void consume(UserEvent event) {
        UserPayload payload = event.getPayload();
        logger.info("Received UserEvent for User ID: {} with action: {}", payload.getUserId(), payload.getAction());

        try {
            SendNotificationRequest request = new SendNotificationRequest();
            request.setRecipientEmail(payload.getEmail());
            request.setRecipientName(payload.getFirstName() + " " + payload.getLastName());
            request.setTitle("Account Update");
            request.setContent("Your account action: " + payload.getAction());
            request.setUserId(payload.getUserId());
            
            request.setParams(new HashMap<>());
            request.getParams().put("userId", payload.getUserId());
            request.getParams().put("action", payload.getAction());
            request.getParams().put("firstName", payload.getFirstName());
            request.getParams().put("lastName", payload.getLastName());

            if ("REGISTERED".equals(payload.getAction())) {
                request.setTemplateId(Long.valueOf(templateProperties.getWelcome()));
                notificationService.sendNotification(request);
                logger.info("Welcome notification sent for user: {}", payload.getUserId());
            } else if ("PASSWORD_CHANGED".equals(payload.getAction())) {
                request.setTemplateId(Long.valueOf(templateProperties.getPasswordChange()));
                notificationService.sendNotification(request);
                logger.info("Password change notification sent for user: {}", payload.getUserId());
            } else {
                notificationService.sendNotification(request);
            }

        } catch (Exception e) {
            logger.error("Error processing UserEvent for user: {}", payload.getUserId(), e);
        }
    }
}
