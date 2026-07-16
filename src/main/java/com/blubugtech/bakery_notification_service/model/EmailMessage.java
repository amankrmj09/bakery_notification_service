package com.blubugtech.bakery_notification_service.model;

import lombok.Builder;
import lombok.Data;
import java.util.Map;
import java.util.List;

@Data
@Builder
public class EmailMessage {
    private String to;
    private String subject;
    private String htmlContent;
    private Map<String, Object> params;
    private String templateName;
}
