package com.blubugtech.bakery_notification_service.dto.notification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class SendNotificationRequest {

    // Getters and Setters
    private UUID userId;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String recipientEmail;

    @Size(max = 100, message = "Recipient name must not exceed 100 characters")
    private String recipientName;

    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title must not exceed 500 characters")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private String recipientPhone;
    private String smsContent;
    private String smsTag;
    private java.util.Set<com.blubugtech.bakery_notification_service.enums.NotificationChannel> channels;

    private Long templateId;

    private java.util.Map<String, Object> params;

    public SendNotificationRequest() {
    }

    public SendNotificationRequest(String recipientEmail, String title, String content) {
        this.recipientEmail = recipientEmail;
        this.title = title;
        this.content = content;
    }

    public static SendNotificationRequest email(String recipientEmail, String title, String content) {
        return new SendNotificationRequest(recipientEmail, title, content);
    }

}
