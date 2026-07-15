package com.blubugtech.bakery_notification_service.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public class SendNotificationRequestDto {

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

    private Long templateId;

    private java.util.Map<String, Object> params;

    public SendNotificationRequestDto() {}

    public SendNotificationRequestDto(String recipientEmail, String title, String content) {
        this.recipientEmail = recipientEmail;
        this.title = title;
        this.content = content;
    }

    public static SendNotificationRequestDto email(String recipientEmail, String title, String content) {
        return new SendNotificationRequestDto(recipientEmail, title, content);
    }

    // Getters and Setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
    public java.util.Map<String, Object> getParams() { return params; }
    public void setParams(java.util.Map<String, Object> params) { this.params = params; }
}
