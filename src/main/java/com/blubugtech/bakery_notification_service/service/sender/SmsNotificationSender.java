package com.blubugtech.bakery_notification_service.service.sender;

import com.blubugtech.bakery_notification_service.enums.NotificationChannel;
import com.blubugtech.bakery_notification_service.model.NotificationRequest;
import com.blubugtech.bakery_notification_service.model.NotificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmsNotificationSender implements NotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(SmsNotificationSender.class);

    @Override
    public boolean supports(NotificationChannel channel) {
        return NotificationChannel.SMS == channel;
    }

    @Override
    public NotificationResult send(NotificationRequest request) {
        logger.info("Simulating SMS Notification to: {}", request.getRecipient());
        return NotificationResult.builder()
                .success(true)
                .messageId("sms-" + System.currentTimeMillis())
                .build();
    }
}
