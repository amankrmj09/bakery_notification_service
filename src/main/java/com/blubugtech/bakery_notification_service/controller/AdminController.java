package com.blubugtech.bakery_notification_service.controller;

import com.blubugtech.bakery_notification_service.service.EmailService;
import com.blubugtech.bakery_notification_service.service.NotificationService;
import com.blubugtech.common.contract.feign.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmailService emailService;

    @Value("${spring.application.name:bakery-notification-service}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String serverPort;



    @Operation(summary = "Test email service")
    @PostMapping("/test/email")
    public ResponseEntity<Map<String, Object>> testEmailService(
            @RequestParam(required = false) String testEmail,
            @RequestHeader(value = "X-User-Id", required = false) String requestingUserId) {

        logger.info("Testing email service: testEmail={}, requester={}", testEmail, requestingUserId);

        try {
            boolean connectivityTest = emailService.testEmailConnection();

            Map<String, Object> result = new HashMap<>();
            result.put("service", "email");
            result.put("connectivity", connectivityTest);
            result.put("timestamp", LocalDateTime.now());

            result.put("health", emailService.getEmailServiceHealth());

            HttpStatus status = connectivityTest ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(result);

        } catch (Exception e) {
            logger.error("Email service test failed: {}", e.getMessage());
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
