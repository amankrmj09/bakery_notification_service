package com.shah_s.bakery_notification_service.service;

import com.shah_s.bakery_notification_service.entity.Notification;
import com.shah_s.bakery_notification_service.exception.NotificationServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.shah_s.bakery_notification_service.dto.BrevoDtos.*;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.UUID;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${brevo.api-key:}")
    private String apiKey;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
            .baseUrl("https://api.brevo.com/v3")
            .defaultHeader("api-key", apiKey)
            .defaultHeader("Content-Type", "application/json")
            .build();
    }

    @Value("${notification.email.from}")
    private String fromEmail;

    @Value("${notification.email.from-name}")
    private String fromName;

    @Value("${notification.email.reply-to}")
    private String replyToEmail;

    @Value("${notification.email.enabled:true}")
    private Boolean emailEnabled;

    @Value("${notification.email.retry-attempts:3}")
    private Integer retryAttempts;

    // Send email notification
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendEmail(Notification notification) {
        if (!emailEnabled) {
            logger.warn("Email notifications are disabled");
            throw new NotificationServiceException("Email notifications are disabled");
        }

        logger.info("Sending email notification: id={}, recipient={}",
                   notification.getId(), notification.getRecipientEmail());

        try {
            if (notification.getHtmlContent() != null && !notification.getHtmlContent().trim().isEmpty()) {
                sendHtmlEmail(notification);
            } else {
                sendTextEmail(notification);
            }

            notification.markAsSent(UUID.randomUUID().toString());
            logger.info("Email sent successfully: {}", notification.getId());

        } catch (Exception e) {
            logger.error("Failed to send email notification {}: {}", notification.getId(), e.getMessage());
            throw new NotificationServiceException("Failed to send email: " + e.getMessage());
        }
    }

    private void sendBrevoEmail(String to, String toName, String subject, String textContent, String htmlContent, Long templateId, Map<String, Object> params) {
        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("Brevo API key is not configured. Email will not be sent.");
            return;
        }

        BrevoEmailRequest request = new BrevoEmailRequest(
            new Sender(fromName, fromEmail),
            List.of(new Recipient(to, toName)),
            subject,
            htmlContent,
            textContent,
            templateId,
            params
        );

        try {
            BrevoEmailResponse response = webClient.post()
                .uri("/smtp/email")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(BrevoEmailResponse.class)
                .block();
            String msgId = response != null ? response.messageId() : null;
            logger.info("Email sent successfully via Brevo. MessageId: {}", msgId);
        } catch (Exception e) {
            logger.error("Failed to send email via Brevo: {}", e.getMessage());
            throw new NotificationServiceException("Brevo API error: " + e.getMessage());
        }
    }

    // Send simple text email
    private void sendTextEmail(Notification notification) {
        try {
            sendBrevoEmail(notification.getRecipientEmail(), notification.getRecipientName(), notification.getSubject(), notification.getContent(), null, null, null);
        } catch (Exception e) {
            logger.error("Failed to send text email: {}", e.getMessage());
            throw new NotificationServiceException("Failed to send text email: " + e.getMessage());
        }
    }

    // Send HTML email
    private void sendHtmlEmail(Notification notification) {
        try {
            sendBrevoEmail(notification.getRecipientEmail(), notification.getRecipientName(), notification.getSubject(), notification.getContent(), notification.getHtmlContent(), null, null);
        } catch (Exception e) {
            logger.error("Failed to send HTML email: {}", e.getMessage());
            throw new NotificationServiceException("Failed to send HTML email: " + e.getMessage());
        }
    }

    // Send templated email
    public void sendTemplatedEmail(String to, String toName, String subject,
                                  Long templateId, Map<String, Object> variables) {
        logger.info("Sending templated email: to={}, templateId={}", to, templateId);

        try {
            sendBrevoEmail(to, toName, subject, null, null, templateId, variables);
            logger.info("Templated email sent successfully: to={}, templateId={}", to, templateId);
        } catch (Exception e) {
            logger.error("Failed to send templated email: {}", e.getMessage());
            throw new NotificationServiceException("Failed to send templated email: " + e.getMessage());
        }
    }

    // Send welcome email
    public void sendWelcomeEmail(String email, String name, String activationLink) {
        Map<String, Object> variables = Map.of(
            "name", name,
            "activationLink", activationLink,
            "supportEmail", replyToEmail
        );
        // TODO ?? set template ID
        sendTemplatedEmail(email, name, "Welcome to Shah's Bakery!", 1L, variables);
    }

    // Send password reset email
    public void sendPasswordResetEmail(String email, String name, String resetLink) {
        Map<String, Object> variables = Map.of(
            "name", name,
            "resetLink", resetLink,
            "supportEmail", replyToEmail,
            "expiryTime", "24 hours"
        );
        // TODO ?? set template ID
        sendTemplatedEmail(email, name, "Password Reset Request", 2L, variables);
    }

    // Send order confirmation email
    public void sendOrderConfirmationEmail(String email, String name, String orderNumber,
                                         String orderTotal, String deliveryType) {
        Map<String, Object> variables = Map.of(
            "name", name,
            "orderNumber", orderNumber,
            "orderTotal", orderTotal,
            "deliveryType", deliveryType,
            "supportEmail", replyToEmail,
            "orderDate", LocalDateTime.now().toString()
        );
        // TODO ?? set template ID
        sendTemplatedEmail(email, name, "Order Confirmation - " + orderNumber, 3L, variables);
    }

    // Send cart abandonment email
    public void sendCartAbandonmentEmail(String email, String name, String cartUrl,
                                       String cartTotal, int itemCount) {
        Map<String, Object> variables = Map.of(
            "name", name,
            "cartUrl", cartUrl,
            "cartTotal", cartTotal,
            "itemCount", itemCount,
            "supportEmail", replyToEmail
        );
        // TODO ?? set template ID
        sendTemplatedEmail(email, name, "Complete Your Order - Items Waiting!", 4L, variables);
    }

    // Send promotional email
    public void sendPromotionalEmail(String email, String name, String promoCode,
                                   String discount, String expiryDate) {
        Map<String, Object> variables = Map.of(
            "name", name,
            "promoCode", promoCode,
            "discount", discount,
            "expiryDate", expiryDate,
            "supportEmail", replyToEmail
        );
        // TODO ?? set template ID
        sendTemplatedEmail(email, name, "Special Offer Just for You!", 5L, variables);
    }

    // Send feedback request email
    public void sendFeedbackRequestEmail(String email, String name, String orderNumber,
                                       String feedbackUrl) {
        Map<String, Object> variables = Map.of(
            "name", name,
            "orderNumber", orderNumber,
            "feedbackUrl", feedbackUrl,
            "supportEmail", replyToEmail
        );
        // TODO ?? set template ID
        sendTemplatedEmail(email, name, "How was your experience?", 6L, variables);
    }

    // Test email connectivity
    public boolean testEmailConnection() {
        if (webClient == null || apiKey == null || apiKey.isEmpty()) {
            return false;
        }
        return true;
    }

    // Get email service health status
    public Map<String, Object> getEmailServiceHealth() {
        return Map.of(
            "enabled", emailEnabled,
            "fromEmail", fromEmail,
            "fromName", fromName,
            "replyTo", replyToEmail,
            "retryAttempts", retryAttempts,
            "connectivity", testEmailConnection(),
            "timestamp", LocalDateTime.now()
        );
    }
}
