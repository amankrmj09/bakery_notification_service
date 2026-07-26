package com.blubugtech.bakery_notification_service.integration.brevo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrevoSmsResponse {
    private Long messageId;
    private String reference;
}
