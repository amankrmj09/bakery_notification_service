package com.blubugtech.bakery_notification_service.service.impl;

import lombok.extern.slf4j.Slf4j;
import com.blubugtech.bakery_notification_service.dto.notification.NotificationResponse;
import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.entity.Notification;
import com.blubugtech.bakery_notification_service.enums.NotificationChannel;
import com.blubugtech.bakery_notification_service.exception.EmailDeliveryException;
import com.blubugtech.bakery_notification_service.mapper.NotificationMapper;
import com.blubugtech.bakery_notification_service.model.NotificationRequest;
import com.blubugtech.bakery_notification_service.model.NotificationResult;
import com.blubugtech.bakery_notification_service.repository.NotificationRepository;
import com.blubugtech.bakery_notification_service.service.NotificationService;
import com.blubugtech.bakery_notification_service.service.sender.NotificationSender;
import com.blubugtech.bakery_notification_service.validation.NotificationValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationValidator notificationValidator;
    private final List<NotificationSender> senders;

    @Value("${notification.sms.enabled:false}")
    private boolean smsEnabled;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            NotificationMapper notificationMapper,
            NotificationValidator notificationValidator,
            List<NotificationSender> senders) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.notificationValidator = notificationValidator;
        this.senders = senders;
    }

    @Override
    public NotificationResponse sendNotification(SendNotificationRequest request) {
        log.info("Sending notification: recipient={}, title={}", request.getRecipientEmail(), request.getTitle());

        if (!notificationValidator.isValid(request)) {
            throw new IllegalArgumentException("Invalid notification request");
        }

        Notification notification = new Notification();
        notification.setRecipientEmail(request.getRecipientEmail());
        notification.setRecipientPhone(request.getRecipientPhone());
        notification.setRecipientName(request.getRecipientName());
        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());

        notification.setStatus(com.blubugtech.bakery_notification_service.entity.Notification.NotificationStatus.PENDING);
        notification = notificationRepository.save(notification);

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

        notification = notificationRepository.save(notification);
        return notificationMapper.toResponse(notification);
    }
}

