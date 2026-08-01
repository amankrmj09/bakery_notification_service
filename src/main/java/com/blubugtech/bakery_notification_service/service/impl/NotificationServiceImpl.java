package com.blubugtech.bakery_notification_service.service.impl;

import lombok.extern.slf4j.Slf4j;
import com.blubugtech.bakery_notification_service.dto.notification.NotificationResponse;
import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.entity.Notification;
import com.blubugtech.bakery_notification_service.mapper.NotificationMapper;
import com.blubugtech.bakery_notification_service.repository.NotificationRepository;
import com.blubugtech.bakery_notification_service.service.NotificationService;
import com.blubugtech.bakery_notification_service.validation.NotificationValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationValidator notificationValidator;
    private final NotificationDispatcher notificationDispatcher;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            NotificationMapper notificationMapper,
            NotificationValidator notificationValidator,
            NotificationDispatcher notificationDispatcher) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.notificationValidator = notificationValidator;
        this.notificationDispatcher = notificationDispatcher;
    }

    @Override
    public NotificationResponse sendNotification(SendNotificationRequest request) {
        log.info("Sending notification: recipient={}, title={}", request.getRecipientEmail(), request.getTitle());

        if (!notificationValidator.isValid(request)) {
            throw new IllegalArgumentException("Invalid notification request");
        }

        Notification notification = notificationMapper.toEntity(request);
        notification.setStatus(com.blubugtech.bakery_notification_service.entity.Notification.NotificationStatus.PENDING);
        notification = notificationRepository.save(notification);

        notificationDispatcher.dispatch(request, notification);

        notification = notificationRepository.save(notification);
        return notificationMapper.toResponse(notification);
    }
}
