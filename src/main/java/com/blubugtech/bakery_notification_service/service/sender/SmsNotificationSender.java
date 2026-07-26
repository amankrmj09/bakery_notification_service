package com.blubugtech.bakery_notification_service.service.sender;

import com.blubugtech.bakery_notification_service.enums.NotificationChannel;
import com.blubugtech.bakery_notification_service.model.NotificationRequest;
import com.blubugtech.bakery_notification_service.model.NotificationResult;
import com.blubugtech.bakery_notification_service.integration.brevo.BrevoSmsClient;
import com.blubugtech.bakery_notification_service.integration.brevo.BrevoSmsRequest;
import com.blubugtech.bakery_notification_service.integration.brevo.BrevoSmsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SmsNotificationSender implements NotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(SmsNotificationSender.class);

    private final BrevoSmsClient brevoSmsClient;
    
    @Value("${brevo.api-key}")
    private String apiKey;
    
    @Value("${brevo.sms.sender:BlusBakery}")
    private String senderName;

    @Value("${brevo.sms.organisation-prefix:BlusBakery}")
    private String orgPrefix;

    public SmsNotificationSender(BrevoSmsClient brevoSmsClient) {
        this.brevoSmsClient = brevoSmsClient;
    }

    @Override
    public boolean supports(NotificationChannel channel) {
        return NotificationChannel.SMS == channel;
    }

    @Override
    public NotificationResult send(NotificationRequest request) {
        logger.info("Sending SMS Notification to: {}", request.getRecipientPhone());
        
        try {
            BrevoSmsRequest smsRequest = BrevoSmsRequest.builder()
                .sender(senderName)
                .recipient(request.getRecipientPhone())
                .content(request.getSmsContent() != null ? request.getSmsContent() : request.getBody())
                .type("transactional")
                .tag(request.getSmsTag())
                .organisationPrefix(orgPrefix)
                .unicodeEnabled(true)
                .build();
                
            BrevoSmsResponse response = brevoSmsClient.sendSms(apiKey, smsRequest).getBody();
            
            return NotificationResult.builder()
                    .success(true)
                    .messageId(response != null && response.getMessageId() != null ? response.getMessageId().toString() : null)
                    .build();
        } catch (Exception e) {
            logger.error("Failed to send SMS via Brevo: {}", e.getMessage());
            return NotificationResult.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }
}
