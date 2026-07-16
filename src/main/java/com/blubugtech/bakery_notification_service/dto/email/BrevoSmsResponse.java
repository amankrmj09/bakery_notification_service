package com.blubugtech.bakery_notification_service.dto.email;

public record BrevoSmsResponse(
    String reference,
    String messageId,
    String smsCount
) {}
