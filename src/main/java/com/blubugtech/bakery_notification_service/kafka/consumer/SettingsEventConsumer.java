package com.blubugtech.bakery_notification_service.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import com.blubugtech.bakery_notification_service.entity.NotificationSettings;
import com.blubugtech.bakery_notification_service.repository.NotificationSettingsRepository;
import org.blubakery.common.messaging.event.SettingsEvent;
import org.blubakery.common.messaging.contract.messaging.SettingsPayload;
import org.blubakery.common.messaging.constants.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SettingsEventConsumer {

    private final NotificationSettingsRepository notificationSettingsRepository;

    public SettingsEventConsumer(NotificationSettingsRepository notificationSettingsRepository) {
        this.notificationSettingsRepository = notificationSettingsRepository;
    }

    @KafkaListener(topics = KafkaTopics.SETTINGS_TOPIC, groupId = "notification-group")
    public void consume(SettingsEvent event) {
        SettingsPayload payload = event.getPayload();
        log.info("Received SettingsEvent with new admin email: {}", payload.getAdminNotificationEmail());

        try {
            List<NotificationSettings> settingsList = notificationSettingsRepository.findAll();
            NotificationSettings settings;
            if (settingsList.isEmpty()) {
                settings = new NotificationSettings();
            } else {
                settings = settingsList.get(0);
            }
            settings.setAdminNotificationEmail(payload.getAdminNotificationEmail());
            notificationSettingsRepository.save(settings);
            log.info("Successfully updated local notification settings");
        } catch (Exception e) {
            log.error("Error processing SettingsEvent", e);
        }
    }
}
