package com.blubugtech.bakery_notification_service.service.sender;

import lombok.extern.slf4j.Slf4j;
import com.blubugtech.bakery_notification_service.enums.NotificationChannel;
import com.blubugtech.bakery_notification_service.model.NotificationRequest;
import com.blubugtech.bakery_notification_service.model.NotificationResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsNotificationSender implements NotificationSender {

    private final com.blubugtech.bakery_notification_service.integration.sms.SmsSender smsSender;

    public SmsNotificationSender(com.blubugtech.bakery_notification_service.integration.sms.SmsSender smsSender) {
        this.smsSender = smsSender;
    }

    @Override
    public boolean supports(NotificationChannel channel) {
        return NotificationChannel.SMS == channel;
    }

    @Override
    public NotificationResult send(NotificationRequest request) {
        log.info("Sending SMS Notification to: {}", request.getRecipientPhone());
        
        String content = request.getSmsContent() != null ? request.getSmsContent() : request.getBody();
        return smsSender.sendSms(request.getRecipientPhone(), content, request.getSmsTag());
    }
}
