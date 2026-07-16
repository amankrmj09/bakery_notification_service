package com.blubugtech.bakery_notification_service.service.impl;

import com.blubugtech.bakery_notification_service.dto.notification.NotificationResponse;
import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.entity.Notification;
import com.blubugtech.bakery_notification_service.enums.NotificationChannel;
import com.blubugtech.bakery_notification_service.enums.NotificationStatus;
import com.blubugtech.bakery_notification_service.enums.NotificationType;
import com.blubugtech.bakery_notification_service.exception.EmailDeliveryException;
import com.blubugtech.bakery_notification_service.mapper.NotificationMapper;
import com.blubugtech.bakery_notification_service.model.NotificationRequest;
import com.blubugtech.bakery_notification_service.model.NotificationResult;
import com.blubugtech.bakery_notification_service.repository.NotificationRepository;
import com.blubugtech.bakery_notification_service.service.NotificationService;
import com.blubugtech.bakery_notification_service.service.sender.NotificationSender;
import com.blubugtech.bakery_notification_service.validation.NotificationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Map;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationValidator notificationValidator;
    private final List<NotificationSender> senders;

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
        logger.info("Sending notification: recipient={}, title={}", request.getRecipientEmail(), request.getTitle());

        if (!notificationValidator.isValid(request)) {
            throw new IllegalArgumentException("Invalid notification request");
        }

        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setRecipientEmail(request.getRecipientEmail());
        notification.setRecipientName(request.getRecipientName());
        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());

        notification.setStatus(com.blubugtech.bakery_notification_service.entity.Notification.NotificationStatus.PENDING);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());

        notification = notificationRepository.save(notification);

        // We will default to EMAIL channel for now
        NotificationChannel channel = NotificationChannel.EMAIL;

        NotificationRequest internalRequest = NotificationRequest.builder()
                .recipient(request.getRecipientEmail())
                .title(request.getTitle())
                .body(request.getContent())
                .channel(channel)
                .templateName(request.getTemplateId() != null ? String.valueOf(request.getTemplateId()) : null)
                .data(request.getParams())
                .build();

        NotificationSender sender = senders.stream()
                .filter(s -> s.supports(channel))
                .findFirst()
                .orElseThrow(() -> new EmailDeliveryException("No sender found for channel: " + channel));

        NotificationResult result = sender.send(internalRequest);

        if (result.isSuccess()) {
            notification.markAsSent(result.getMessageId());
        } else {
            notification.markAsFailed(result.getErrorMessage());
            throw new EmailDeliveryException("Failed to send notification: " + result.getErrorMessage());
        }

        notification = notificationRepository.save(notification);
        return notificationMapper.toResponse(notification);
    }
}

