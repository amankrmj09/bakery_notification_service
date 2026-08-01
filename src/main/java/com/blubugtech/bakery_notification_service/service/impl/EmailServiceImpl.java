package com.blubugtech.bakery_notification_service.service.impl;

import lombok.extern.slf4j.Slf4j;
import com.blubugtech.bakery_notification_service.service.EmailService;
import com.blubugtech.bakery_notification_service.integration.email.EmailSender;
import com.blubugtech.bakery_notification_service.model.EmailMessage;
import com.blubugtech.bakery_notification_service.model.NotificationResult;
import com.blubugtech.bakery_notification_service.entity.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.HashMap;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final EmailSender emailSender;
    private final String ownerEmail;
    private final String templateIdAck;
    private final String templateIdNotify;

    public EmailServiceImpl(
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
    public void sendEmail(Notification notification, Map<String, Object> customParams) {
        Map<String, Object> params = new HashMap<>();
        if (customParams != null) {
            params.putAll(customParams);
        }
        params.putIfAbsent("name", notification.getRecipientName() != null ? notification.getRecipientName() : "User");
        params.putIfAbsent("content", notification.getContent() != null ? notification.getContent() : "");
        params.putIfAbsent("title", notification.getTitle() != null ? notification.getTitle() : "");

        EmailMessage message = EmailMessage.builder()
                .to(notification.getRecipientEmail())
                .templateName(notification.getTemplateId() != null ? String.valueOf(notification.getTemplateId()) : templateIdNotify)
                .params(params)
                .build();
                
        NotificationResult result = emailSender.send(message);
        if (result.isSuccess()) {
            log.info("Notification email sent to {}. Message ID: {}", notification.getRecipientEmail(), result.getMessageId());
            notification.markAsSent(result.getMessageId());
        } else {
            log.error("Failed to send notification email to {}: {}", notification.getRecipientEmail(), result.getErrorMessage());
            notification.markAsFailed(result.getErrorMessage());
        }
    }

}
