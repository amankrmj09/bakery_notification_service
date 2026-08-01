package com.blubugtech.bakery_notification_service.integration.brevo;

import lombok.extern.slf4j.Slf4j;
import com.blubugtech.bakery_notification_service.integration.sms.SmsSender;
import com.blubugtech.bakery_notification_service.model.NotificationResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BrevoSmsSenderImpl implements SmsSender {

    private final BrevoSmsClient brevoSmsClient;

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${brevo.sms.sender:BlusBakery}")
    private String senderName;

    @Value("${brevo.sms.organisation-prefix:BlusBakery}")
    private String orgPrefix;

    public BrevoSmsSenderImpl(BrevoSmsClient brevoSmsClient) {
        this.brevoSmsClient = brevoSmsClient;
    }

    @Override
    public NotificationResult sendSms(String recipientPhone, String content, String tag) {
        log.info("Sending SMS via Brevo to: {}", recipientPhone);

        try {
            String formattedPhone = recipientPhone != null ? recipientPhone.replaceAll("[^0-9]", "") : "";

            BrevoSmsRequest smsRequest = BrevoSmsRequest.builder()
                    .sender(senderName)
                    .recipient(formattedPhone)
                    .content(content)
                    .type("transactional")
                    .tag(tag)
                    .organisationPrefix(orgPrefix)
                    .unicodeEnabled(true)
                    .build();

            BrevoSmsResponse response = brevoSmsClient.sendSms(apiKey, smsRequest).getBody();

            return NotificationResult.builder()
                    .success(true)
                    .messageId(response != null && response.getMessageId() != null ? response.getMessageId().toString() : null)
                    .build();
        } catch (Exception e) {
            log.error("Failed to send SMS via Brevo", e);
            return NotificationResult.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }
}
