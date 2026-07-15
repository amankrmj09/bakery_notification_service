package com.blubugtech.bakery_notification_service.dto;

import java.util.List;
import java.util.Map;

public class BrevoEmailDto {

    public record Recipient(String email, String name) {}

    public record Sender(String name, String email) {}

    public record BrevoTemplateEmailRequest(
            List<Recipient> to,
            Long templateId,
            Map<String, Object> params
    ) {}

    public record BrevoEmailResponse(String messageId) {}
}
