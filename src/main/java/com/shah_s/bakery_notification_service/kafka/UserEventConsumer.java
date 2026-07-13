package com.shah_s.bakery_notification_service.kafka;

import org.devofblue.common.event.UserEvent;
import com.shah_s.bakery_notification_service.config.BrevoTemplateProperties;
import com.shah_s.bakery_notification_service.dto.SendNotificationRequestDto;
import com.shah_s.bakery_notification_service.entity.Notification;
import com.shah_s.bakery_notification_service.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);
    private final NotificationService notificationService;
    private final BrevoTemplateProperties templateProperties;

    public UserEventConsumer(NotificationService notificationService, BrevoTemplateProperties templateProperties) {
        this.notificationService = notificationService;
        this.templateProperties = templateProperties;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-service-group")
    public void consume(UserEvent event) {
        logger.info("Received UserEvent for User ID: {} with action: {}", event.getUserId(), event.getAction());
        
        try {
            SendNotificationRequestDto request = new SendNotificationRequestDto();
            request.setRecipientEmail(event.getEmail());
            request.setRecipientName(event.getFirstName() + " " + event.getLastName());
            request.setUserId(event.getUserId());
            
            // Set dynamic parameters
            request.getParams().put("userId", event.getUserId());
            request.getParams().put("action", event.getAction());
            request.getParams().put("firstName", event.getFirstName());
            request.getParams().put("lastName", event.getLastName());
            
            if ("REGISTERED".equals(event.getAction())) {
                request.setTitle("Welcome to Bakery");
                request.setTemplateId(templateProperties.getWelcome());
                notificationService.sendNotification(request);
                logger.info("Welcome notification sent for user: {}", event.getUserId());
            } else if ("PASSWORD_CHANGED".equals(event.getAction())) {
                request.setTitle("Password Changed");
                request.setTemplateId(templateProperties.getPasswordChange());
                notificationService.sendNotification(request);
                logger.info("Password change notification sent for user: {}", event.getUserId());
            }
        } catch (Exception e) {
            logger.error("Failed to process user event for notification: {}", e.getMessage());
        }
    }
}
