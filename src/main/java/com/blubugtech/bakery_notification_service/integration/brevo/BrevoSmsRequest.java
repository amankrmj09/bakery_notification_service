package com.blubugtech.bakery_notification_service.integration.brevo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrevoSmsRequest {
    private String sender;
    private String recipient;
    private String content;
    private String type;
    private String tag;
    private String organisationPrefix;
    private Boolean unicodeEnabled;
}
