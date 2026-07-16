package com.blubugtech.bakery_notification_service.dto.email;

public record BrevoSmsRequest(
    String sender,
    String recipient,
    String content,
    String type
) {}
