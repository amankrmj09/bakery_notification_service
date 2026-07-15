package com.blubugtech.bakery_notification_service.controller;

import com.blubugtech.bakery_notification_service.dto.NotificationResponseDto;
import com.blubugtech.bakery_notification_service.dto.SendNotificationRequestDto;
import com.blubugtech.bakery_notification_service.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/notifications")
@Validated
@Tag(name = "Notifications", description = "Notification management APIs")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "Send an email notification")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM') or hasRole('MARKETING')")
    public ResponseEntity<NotificationResponseDto> sendNotification(
            @Valid @RequestBody SendNotificationRequestDto request,
            @RequestHeader(value = "X-User-Id", required = false) String requestingUserId) {

        logger.info("Sending email notification: recipient={}, requester={}",
                request.getRecipientEmail(), requestingUserId);

        NotificationResponseDto response = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Send bulk email notifications")
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM') or hasRole('MARKETING')")
    public ResponseEntity<com.blubugtech.common.dto.MessageResponseDto> sendBulkNotifications(
            @Valid @RequestBody List<SendNotificationRequestDto> requests,
            @RequestHeader(value = "X-User-Id", required = false) String requestingUserId) {

        logger.info("Sending bulk notifications: count={}, requester={}", requests.size(), requestingUserId);

        CompletableFuture<List<NotificationResponseDto>> future =
                notificationService.sendBulkNotifications(requests);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new com.blubugtech.common.dto.MessageResponseDto("Bulk notifications accepted for processing. Count: " + requests.size()));
    }

    @Operation(summary = "Get notification by ID")
    @GetMapping("/{notificationId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM') or hasRole('USER')")
    public ResponseEntity<NotificationResponseDto> getNotificationById(
            @PathVariable UUID notificationId,
            @RequestHeader(value = "X-User-Id", required = false) String requestingUserId) {

        NotificationResponseDto notification = notificationService.getNotificationById(notificationId);
        return ResponseEntity.ok(notification);
    }

    @Operation(summary = "Get notifications by user")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM') or (#userId.toString() == authentication.name)")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByUser(
            @PathVariable UUID userId,
            @RequestHeader(value = "X-User-Id", required = false) String requestingUserId) {

        List<NotificationResponseDto> notifications = notificationService.getNotificationsByUser(userId);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "Get notifications by user with pagination")
    @GetMapping("/user/{userId}/paginated")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM') or (#userId.toString() == authentication.name)")
    public ResponseEntity<Page<NotificationResponseDto>> getNotificationsByUserPaginated(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        Page<NotificationResponseDto> notifications = notificationService.getNotificationsByUser(
                userId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "Health check")
    @GetMapping("/health")
    public ResponseEntity<com.blubugtech.common.dto.HealthResponseDto> healthCheck() {
        return ResponseEntity.ok(new com.blubugtech.common.dto.HealthResponseDto("UP", "notification-service"));
    }
}
