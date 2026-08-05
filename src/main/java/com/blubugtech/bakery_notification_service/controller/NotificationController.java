package com.blubugtech.bakery_notification_service.controller;

import lombok.extern.slf4j.Slf4j;
import com.blubugtech.bakery_notification_service.dto.notification.NotificationResponse;
import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.blubakery.common.feign.contract.feign.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@Validated
@Tag(name = "Notifications", description = "Notification management APIs")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Send an email notification")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM') or hasRole('MARKETING')")
    public ResponseEntity<NotificationResponse> sendNotification(
            @Valid @RequestBody SendNotificationRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String requestingUserId) {

        log.info("Sending email notification: recipient={}, requester={}",
                request.getRecipientEmail(), requestingUserId);

        NotificationResponse response = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Send bulk email notifications")
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM') or hasRole('MARKETING')")
    public ResponseEntity<MessageResponse> sendBulkNotifications(
            @Valid @RequestBody List<SendNotificationRequest> requests,
            @RequestHeader(value = "X-User-Id", required = false) String requestingUserId) {

        log.info("Sending bulk notifications: count={}, requester={}", requests.size(), requestingUserId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new MessageResponse("Bulk notifications accepted for processing. Count: " + requests.size()));
    }

    @Operation(summary = "Get notification by ID")
    @GetMapping("/{notificationId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM') or hasRole('USER')")
    public ResponseEntity<NotificationResponse> getNotificationById(
            @PathVariable UUID notificationId,
            @RequestHeader(value = "X-User-Id", required = false) String requestingUserId) {

        NotificationResponse notification = null;
        return ResponseEntity.ok(notification);
    }

    @Operation(summary = "Get notifications by user")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM') or (#userId.toString() == authentication.name)")
    public ResponseEntity<PagedModel<NotificationResponse>> getNotificationsByUser(
            @PathVariable UUID userId,
            @RequestHeader(value = "X-User-Id", required = false) String requestingUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.fromString(sortDir), sortBy);
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);
        
        PagedModel<NotificationResponse> notifications = notificationService.getNotificationsByUser(userId, pageable);
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "Get notifications by user with pagination")
    @GetMapping("/user/{userId}/paginated")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM') or (#userId.toString() == authentication.name)")
    public ResponseEntity<PagedModel<NotificationResponse>> getNotificationsByUserPaginated(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.fromString(sortDir), sortBy);
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);
        
        PagedModel<NotificationResponse> notifications = notificationService.getNotificationsByUser(userId, pageable);
        return ResponseEntity.ok(notifications);
    }

}
