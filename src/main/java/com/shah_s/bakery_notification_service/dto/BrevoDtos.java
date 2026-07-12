package com.shah_s.bakery_notification_service.dto;

import java.util.List;

public class BrevoDtos {

    public record Sender(String name, String email) {}
    
    public record Recipient(String email, String name) {}

    public record BrevoEmailRequest(
        Sender sender,
        List<Recipient> to,
        String subject,
        String htmlContent,
        String textContent,
        Long templateId,
        java.util.Map<String, Object> params
    ) {}

    public record BrevoEmailResponse(String messageId) {}

    public record BrevoSmsRequest(
        String sender,
        String recipient,
        String content,
        String type
    ) {}

    public record BrevoSmsResponse(
        String reference,
        String messageId,
        String smsCount
    ) {}
}
