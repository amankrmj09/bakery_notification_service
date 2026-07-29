package com.blubugtech.bakery_notification_service.integration.brevo;

import lombok.extern.slf4j.Slf4j;
import com.blubugtech.bakery_notification_service.dto.email.BrevoEmailRequest;
import com.blubugtech.bakery_notification_service.dto.email.BrevoEmailResponse;
import com.blubugtech.bakery_notification_service.dto.email.BrevoParticipant;
import com.blubugtech.bakery_notification_service.model.EmailMessage;
import com.blubugtech.bakery_notification_service.model.NotificationResult;
import com.blubugtech.bakery_notification_service.integration.email.EmailSender;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class BrevoEmailSender implements EmailSender {
    
    private final BrevoEmailClient brevoEmailClient;
    private final BrevoEmailProperties brevoProperties;

    public BrevoEmailSender(BrevoEmailClient brevoEmailClient, BrevoEmailProperties brevoProperties) {
        this.brevoEmailClient = brevoEmailClient;
        this.brevoProperties = brevoProperties;
    }

    @Override
    public NotificationResult send(EmailMessage message) {
        log.info("Sending email via Brevo to: {}", message.getTo());
        try {
            Long templateId = null;
            if (message.getTemplateName() != null) {
                try {
                    templateId = Long.parseLong(message.getTemplateName());
                } catch (NumberFormatException ignored) {}
            }

            BrevoEmailRequest request = new BrevoEmailRequest(
                null, // sender can be null for template emails in Brevo
                List.of(new BrevoParticipant(null, message.getTo())),
                message.getSubject(),
                message.getHtmlContent(),
                null,
                templateId,
                message.getParams()
            );
            
            // BrevoEmailClient uses RestClient (synchronous)
            BrevoEmailResponse response = brevoEmailClient.sendTemplateEmail(brevoProperties.getApiKey(), request).getBody();

            return NotificationResult.builder()
                    .success(true)
                    .messageId(response != null ? response.messageId() : null)
                    .build();
        } catch (Exception e) {
            log.error("Failed to send email via Brevo", e);
            return NotificationResult.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

}
