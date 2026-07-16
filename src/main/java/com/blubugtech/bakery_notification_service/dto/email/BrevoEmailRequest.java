package com.blubugtech.bakery_notification_service.dto.email;

import java.util.List;
import java.util.Map;

public record BrevoEmailRequest(
    BrevoParticipant sender,
    List<BrevoParticipant> to,
    String subject,
    String htmlContent,
    String textContent,
    Long templateId,
    Map<String, Object> params
) {}
