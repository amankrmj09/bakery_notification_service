package com.blubugtech.bakery_notification_service.service.sender;

import com.blubugtech.bakery_notification_service.enums.NotificationChannel;
import com.blubugtech.bakery_notification_service.integration.email.EmailSender;
import com.blubugtech.bakery_notification_service.model.EmailMessage;
import com.blubugtech.bakery_notification_service.model.NotificationRequest;
import com.blubugtech.bakery_notification_service.model.NotificationResult;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationSender implements NotificationSender {

    private final EmailSender emailSender;

    public EmailNotificationSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public boolean supports(NotificationChannel channel) {
        return NotificationChannel.EMAIL == channel;
    }

    @Override
    public NotificationResult send(NotificationRequest request) {
        EmailMessage emailMessage = EmailMessage.builder()
                .to(request.getRecipient())
                .subject(request.getTitle())
                .htmlContent(request.getBody())
                .params(request.getData())
                .templateName(request.getTemplateName())
                .build();
                
        return emailSender.send(emailMessage);
    }
}
