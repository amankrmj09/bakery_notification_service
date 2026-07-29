package com.blubugtech.bakery_notification_service.service.sender;

import lombok.extern.slf4j.Slf4j;
import com.blubugtech.bakery_notification_service.enums.NotificationChannel;
import com.blubugtech.bakery_notification_service.model.NotificationRequest;
import com.blubugtech.bakery_notification_service.model.NotificationResult;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PushNotificationSender implements NotificationSender {

    @Override
    public boolean supports(NotificationChannel channel) {
        return NotificationChannel.PUSH == channel;
    }

    @Override
    public NotificationResult send(NotificationRequest request) {
        log.info("Simulating Push Notification to: {}", request.getRecipient());
        return NotificationResult.builder()
                .success(true)
                .messageId("push-" + System.currentTimeMillis())
                .build();
    }
}
