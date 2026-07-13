package com.shah_s.bakery_notification_service.controller;

import com.shah_s.bakery_notification_service.dto.NotificationResponseDto;
import com.shah_s.bakery_notification_service.dto.SendNotificationRequestDto;
import com.shah_s.bakery_notification_service.service.NotificationService;
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

        try {
            NotificationResponseDto response = notificationService.sendNotification(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Failed to send notification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Send bulk email notifications")
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM') or hasRole('MARKETING')")
    public ResponseEntity<Map<String, Object>> sendBulkNotifications(
            @Valid @RequestBody List<SendNotificationRequestDto> requests,
            @RequestHeader(value = "X-User-Id", required = false) String requestingUserId) {

        logger.info("Sending bulk notifications: count={}, requester={}", requests.size(), requestingUserId);

        try {
            CompletableFuture<List<NotificationResponseDto>> future =
                    notificationService.sendBulkNotifications(requests);

            Map<String, Object> response = Map.of(
                    "status", "ACCEPTED",
                    "count", requests.size(),
                    "message", "Bulk notifications accepted for processing"
            );

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } catch (Exception e) {
            logger.error("Failed to process bulk notifications: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get notification by ID")
    @GetMapping("/{notificationId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM') or hasRole('USER')")
    public ResponseEntity<NotificationResponseDto> getNotificationById(
            @PathVariable UUID notificationId,
            @RequestHeader(value = "X-User-Id", required = false) String requestingUserId) {

        try {
            NotificationResponseDto notification = notificationService.getNotificationById(notificationId);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get notifications by user")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM') or (#userId.toString() == authentication.name)")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByUser(
            @PathVariable UUID userId,
            @RequestHeader(value = "X-User-Id", required = false) String requestingUserId) {

        try {
            List<NotificationResponseDto> notifications = notificationService.getNotificationsByUser(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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

        try {
            Page<NotificationResponseDto> notifications = notificationService.getNotificationsByUser(
                    userId, page, size, sortBy, sortDir);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Health check")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = Map.of(
                "status", "UP",
                "service", "notification-service",
                "timestamp", LocalDateTime.now(),
                "version", "1.0.0"
        );
        return ResponseEntity.ok(health);
    }
}
