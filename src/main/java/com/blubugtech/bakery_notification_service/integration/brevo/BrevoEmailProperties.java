package com.blubugtech.bakery_notification_service.integration.brevo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "notification.email")
public class BrevoEmailProperties {
    private String from;
    private String replyTo;
    private String senderName;
    private String apiKey; // Assuming this is defined somewhere, maybe brevo.api-key

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    public String getReplyTo() { return replyTo; }
    public void setReplyTo(String replyTo) { this.replyTo = replyTo; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
}
