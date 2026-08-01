package com.blubugtech.bakery_notification_service.controller;

import lombok.extern.slf4j.Slf4j;
import com.blubugtech.bakery_notification_service.service.EmailService;
import com.blubugtech.bakery_notification_service.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Validated
@Tag(name = "Admin", description = "System administration and monitoring APIs")
@PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final NotificationService notificationService;

    private final com.blubugtech.bakery_notification_service.service.EmailHealthCheck emailHealthCheck;

    @Value("${spring.application.name:bakery-notification-service}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String serverPort;


    @Operation(summary = "Test email service")
    @PostMapping("/test/email")
    public ResponseEntity<Map<String, Object>> testEmailService(
            @RequestParam(required = false) String testEmail,
            @RequestHeader(value = "X-User-Id", required = false) String requestingUserId) {

        log.info("Testing email service: testEmail={}, requester={}", testEmail, requestingUserId);

        try {
            boolean connectivityTest = emailHealthCheck.testEmailConnection();

            Map<String, Object> result = new HashMap<>();
            result.put("service", "email");
            result.put("connectivity", connectivityTest);
            result.put("timestamp", LocalDateTime.now());

            result.put("health", emailHealthCheck.getEmailServiceHealth());

            HttpStatus status = connectivityTest ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(result);

        } catch (Exception e) {
            log.error("Email service test failed", e);
            Map<String, Object> errorResult = Map.of(
                    "service", "email",
                    "status", "ERROR",
                    "error", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }


}
