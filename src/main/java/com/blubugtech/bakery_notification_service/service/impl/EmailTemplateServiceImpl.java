package com.blubugtech.bakery_notification_service.service.impl;

import com.blubugtech.bakery_notification_service.integration.email.EmailSender;
import com.blubugtech.bakery_notification_service.model.EmailMessage;
import com.blubugtech.bakery_notification_service.model.NotificationResult;
import com.blubugtech.bakery_notification_service.service.EmailTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final EmailSender emailSender;
    private final String ownerEmail;
    private final String templateIdAck;
    private final String templateIdNotify;

    public EmailTemplateServiceImpl(
            EmailSender emailSender,
            @Value("${notification.email.reply-to}") String ownerEmail,
            @Value("${brevo.template-id-ack:1}") String templateIdAck,
            @Value("${brevo.template-id-notify:2}") String templateIdNotify) {
        this.emailSender = emailSender;
        this.ownerEmail = ownerEmail;
        this.templateIdAck = templateIdAck;
        this.templateIdNotify = templateIdNotify;
    }

    @Override
    public void sendAutoReplyToUser(String userName, String userEmail) {
        EmailMessage message = EmailMessage.builder()
                .to(userEmail)
                .templateName(templateIdAck)
                .params(Map.of("name", userName))
                .build();
                
        NotificationResult result = emailSender.send(message);
        if (result.isSuccess()) {
            log.info("Auto-reply sent to {}. Message ID: {}", userEmail, result.getMessageId());
        } else {
            log.error("Failed to send auto-reply to {}: {}", userEmail, result.getErrorMessage());
        }
    }

    @Override
    public void sendNotificationToAdmin(String userName, String userEmail, String phone, String instagramId, String messageContent) {
        EmailMessage message = EmailMessage.builder()
                .to(ownerEmail)
                .templateName(templateIdNotify)
                .params(Map.of(
                        "name", userName,
                        "email", userEmail,
                        "phone", phone != null ? phone : "N/A",
                        "instagramId", instagramId != null ? instagramId : "N/A",
                        "message", messageContent
                ))
                .build();
                
        NotificationResult result = emailSender.send(message);
        if (result.isSuccess()) {
            log.info("Admin notification sent for contact from {}. Message ID: {}", userName, result.getMessageId());
        } else {
            log.error("Failed to send admin notification for {}: {}", userName, result.getErrorMessage());
        }
    }
}
