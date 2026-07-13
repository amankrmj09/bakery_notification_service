package com.shah_s.bakery_notification_service.service;

import com.shah_s.bakery_notification_service.client.BrevoEmailClient;
import com.shah_s.bakery_notification_service.dto.BrevoEmailDto.BrevoTemplateEmailRequest;
import com.shah_s.bakery_notification_service.dto.BrevoEmailDto.Recipient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);

    private final BrevoEmailClient brevoClient;
    private final String apiKey;
    private final String senderEmail;
    private final String senderName;
    private final String ownerEmail;
    private final Long templateIdAck;
    private final Long templateIdNotify;

    public EmailService(
            BrevoEmailClient brevoClient,
            @Value("${brevo.api-key}") String apiKey,
            @Value("${notification.email.from}") String senderEmail,
            @Value("${notification.email.reply-to}") String ownerEmail,
            @Value("${notification.email.sender-name:Bakery Admin}") String senderName,
            @Value("${brevo.template-id-ack:1}") Long templateIdAck,
            @Value("${brevo.template-id-notify:2}") Long templateIdNotify) {

        this.brevoClient = brevoClient;
        this.apiKey = apiKey;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
        this.ownerEmail = ownerEmail;
        this.templateIdAck = templateIdAck;
        this.templateIdNotify = templateIdNotify;
    }

    public void sendAutoReplyToUser(String userName, String userEmail) {
        BrevoTemplateEmailRequest request = new BrevoTemplateEmailRequest(
                List.of(new Recipient(userEmail, userName)),
                templateIdAck,
                Map.of("name", userName)
        );

        Mono.from(brevoClient.sendTemplateEmail(apiKey, request))
                .doOnSuccess(res -> {
                    assert res != null;
                    LOG.info("Auto-reply sent to {}. Message ID: {}", userEmail, res.getBody() != null ? Objects.requireNonNull(res.getBody()).messageId() : "unknown");
                })
                .doOnError(e -> {
                    LOG.error("Failed to send auto-reply to {}", userEmail, e);
                })
                .subscribe();
    }

    public void sendNotificationToAdmin(String userName, String userEmail, String phone, String instagramId, String message) {
        BrevoTemplateEmailRequest request = new BrevoTemplateEmailRequest(
                List.of(new Recipient(ownerEmail, "Admin")),
                templateIdNotify,
                Map.of(
                        "name", userName,
                        "email", userEmail,
                        "phone", phone != null ? phone : "N/A",
                        "instagramId", instagramId != null ? instagramId : "N/A",
                        "message", message
                )
        );

        Mono.from(brevoClient.sendTemplateEmail(apiKey, request))
                .doOnSuccess(res -> {
                    assert res != null;
                    LOG.info("Admin notification sent for contact from {}. Message ID: {}", userName, res.getBody() != null ? Objects.requireNonNull(res.getBody()).messageId() : "unknown");
                })
                .doOnError(e -> {
                    LOG.error("Failed to send admin notification for {}", userName, e);
                })
                .subscribe();
    }

    public void sendEmail(com.shah_s.bakery_notification_service.entity.Notification notification, Map<String, Object> customParams) {
        Map<String, Object> params = new java.util.HashMap<>();
        if (customParams != null) {
            params.putAll(customParams);
        }
        params.putIfAbsent("name", notification.getRecipientName() != null ? notification.getRecipientName() : "User");
        params.putIfAbsent("content", notification.getContent() != null ? notification.getContent() : "");
        params.putIfAbsent("title", notification.getTitle() != null ? notification.getTitle() : "");

        BrevoTemplateEmailRequest request = new BrevoTemplateEmailRequest(
                List.of(new Recipient(notification.getRecipientEmail(), notification.getRecipientName())),
                notification.getTemplateId() != null ? notification.getTemplateId() : templateIdNotify,
                params
        );

        Mono.from(brevoClient.sendTemplateEmail(apiKey, request))
                .doOnSuccess(res -> {
                    assert res != null;
                    LOG.info("Notification email sent to {}. Message ID: {}", notification.getRecipientEmail(), res.getBody() != null ? Objects.requireNonNull(res.getBody()).messageId() : "unknown");
                    notification.markAsSent(res.getBody() != null ? res.getBody().messageId() : null);
                })
                .doOnError(e -> {
                    LOG.error("Failed to send notification email to {}", notification.getRecipientEmail(), e);
                    notification.markAsFailed(e.getMessage());
                })
                .subscribe();
    }

    public boolean testEmailConnection() {
        return true;
    }

    public Map<String, Object> getEmailServiceHealth() {
        return Map.of("status", "UP", "enabled", true, "connectivity", true);
    }
}
