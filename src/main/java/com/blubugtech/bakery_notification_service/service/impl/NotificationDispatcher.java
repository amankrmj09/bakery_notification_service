package com.blubugtech.bakery_notification_service.service.impl;

import lombok.extern.slf4j.Slf4j;
import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.entity.Notification;
import com.blubugtech.bakery_notification_service.enums.NotificationChannel;
import com.blubugtech.bakery_notification_service.exception.EmailDeliveryException;
import com.blubugtech.bakery_notification_service.model.NotificationRequest;
import com.blubugtech.bakery_notification_service.model.NotificationResult;
import com.blubugtech.bakery_notification_service.service.sender.NotificationSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class NotificationDispatcher {

    private final List<NotificationSender> senders;

    @Value("${notification.sms.enabled:false}")
    private boolean smsEnabled;

    public NotificationDispatcher(List<NotificationSender> senders) {
        this.senders = senders;
    }

    public void dispatch(SendNotificationRequest request, Notification notification) {
        Set<NotificationChannel> channels = request.getChannels();
        if (channels == null || channels.isEmpty()) {
            channels = Set.of(NotificationChannel.EMAIL);
        }

        NotificationRequest internalRequest = NotificationRequest.builder()
                .recipient(request.getRecipientEmail())
                .recipientPhone(request.getRecipientPhone())
                .title(request.getTitle())
                .body(request.getContent())
                .smsContent(request.getSmsContent())
                .smsTag(request.getSmsTag())
                .channels(channels)
                .templateName(request.getTemplateId() != null ? String.valueOf(request.getTemplateId()) : null)
                .data(request.getParams())
                .build();

        boolean allFailed = true;
        StringBuilder errorMessages = new StringBuilder();

        for (NotificationChannel channel : channels) {
            if (channel == NotificationChannel.SMS && !smsEnabled) {
                log.info("SMS is disabled, skipping channel");
                continue;
            }

            NotificationSender sender = senders.stream()
                    .filter(s -> s.supports(channel))
                    .findFirst()
                    .orElse(null);

            if (sender == null) {
                log.warn("No sender found for channel: {}", channel);
                errorMessages.append("No sender for ").append(channel).append("; ");
                continue;
            }

            try {
                NotificationResult result = sender.send(internalRequest);
                if (result.isSuccess()) {
                    allFailed = false;
                    if (channel == NotificationChannel.EMAIL) {
                        notification.setEmailMessageId(result.getMessageId());
                    } else if (channel == NotificationChannel.SMS) {
                        notification.setSmsMessageId(result.getMessageId());
                    }
                } else {
                    errorMessages.append(channel).append(" failed: ").append(result.getErrorMessage()).append("; ");
                }
            } catch (Exception e) {
                log.error("Error sending notification via channel: {}", channel, e);
                errorMessages.append(channel).append(" error: ").append(e.getMessage()).append("; ");
            }
        }

        if (allFailed && !errorMessages.isEmpty()) {
            notification.markAsFailed(errorMessages.toString());
            throw new EmailDeliveryException("Failed to send notification: " + errorMessages);
        } else {
            notification.setStatus(com.blubugtech.bakery_notification_service.entity.Notification.NotificationStatus.SENT);
            notification.setSentAt(java.time.LocalDateTime.now());
            if (!errorMessages.isEmpty()) {
                notification.setErrorMessage(errorMessages.toString()); // Partial failure
            }
        }
    }
}
