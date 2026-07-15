package com.blubugtech.bakery_notification_service.service;

import com.blubugtech.bakery_notification_service.dto.NotificationResponseDto;
import com.blubugtech.bakery_notification_service.dto.SendNotificationRequestDto;
import com.blubugtech.bakery_notification_service.entity.Notification;
import com.blubugtech.bakery_notification_service.exception.NotificationServiceException;
import com.blubugtech.bakery_notification_service.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    // Send notification
    public NotificationResponseDto sendNotification(SendNotificationRequestDto request) {
        logger.info("Sending email notification: recipientEmail={}, title={}",
                   request.getRecipientEmail(), request.getTitle());

        try {
            // Create notification entity
            Notification notification = createNotificationFromRequest(request);

            // Validate notification
            validateNotification(notification);

            // Save notification as PENDING
            notification = notificationRepository.save(notification);

            // Send via email service
            sendNotificationNow(notification, request.getParams());

            logger.info("Notification processed successfully: {}", notification.getId());
            return NotificationResponseDto.from(notification);

        } catch (Exception e) {
            logger.error("Failed to send notification: {}", e.getMessage(), e);
            throw new NotificationServiceException("Failed to send notification: " + e.getMessage());
        }
    }

    // Send bulk notifications
    @Async
    public CompletableFuture<List<NotificationResponseDto>> sendBulkNotifications(
            List<SendNotificationRequestDto> requests) {
        logger.info("Sending bulk email notifications: count={}", requests.size());

        try {
            List<NotificationResponseDto> responses = new ArrayList<>();

            for (SendNotificationRequestDto request : requests) {
                try {
                    NotificationResponseDto response = sendNotification(request);
                    responses.add(response);
                } catch (Exception e) {
                    logger.error("Failed to send notification in bulk: {}", e.getMessage());
                    // Continue with other notifications
                }
            }

            logger.info("Bulk notifications completed: sent={}/{}", responses.size(), requests.size());
            return CompletableFuture.completedFuture(responses);

        } catch (Exception e) {
            logger.error("Failed to send bulk notifications: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Transactional(readOnly = true)
    public NotificationResponseDto getNotificationById(UUID notificationId) {
        logger.debug("Getting notification by ID: {}", notificationId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationServiceException("Notification not found: " + notificationId));
        return NotificationResponseDto.from(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByUser(UUID userId) {
        logger.debug("Getting notifications by user: {}", userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponseDto> getNotificationsByUser(UUID userId, int page, int size, String sortBy, String sortDir) {
        logger.debug("Getting notifications by user with pagination: userId={}, page={}, size={}", userId, page, size);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(NotificationResponseDto::from);
    }

    private Notification createNotificationFromRequest(SendNotificationRequestDto request) {
        String title = request.getTitle() != null ? request.getTitle() : "Notification";
        String content = request.getContent() != null ? request.getContent() : "Template-based notification content";
        
        Notification notification = new Notification(title, content, request.getRecipientEmail());
        notification.setUserId(request.getUserId());
        notification.setRecipientName(request.getRecipientName());
        notification.setTemplateId(request.getTemplateId());
        return notification;
    }

    private void validateNotification(Notification notification) {
        if (notification.getRecipientEmail() == null || notification.getRecipientEmail().trim().isEmpty()) {
            throw new NotificationServiceException("Email address is required for notifications");
        }
        // Relaxing title and content validation as we heavily rely on Brevo templates now
    }

    private void sendNotificationNow(Notification notification, java.util.Map<String, Object> params) {
        try {
            emailService.sendEmail(notification, params);
            notificationRepository.save(notification);
        } catch (Exception e) {
            logger.error("Failed to send notification {}: {}", notification.getId(), e.getMessage());
            notification.markAsFailed(e.getMessage());
            notificationRepository.save(notification);
            throw e;
        }
    }
}
