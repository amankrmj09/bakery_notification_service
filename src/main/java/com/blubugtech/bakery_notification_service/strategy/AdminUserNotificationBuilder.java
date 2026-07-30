package com.blubugtech.bakery_notification_service.strategy;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.integration.brevo.BrevoTemplateProperties;
import org.blubakery.common.messaging.contract.messaging.UserPayload;
import com.blubugtech.bakery_notification_service.entity.NotificationSettings;
import com.blubugtech.bakery_notification_service.repository.NotificationSettingsRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AdminUserNotificationBuilder extends BaseNotificationBuilder<UserPayload> {

    private final BrevoTemplateProperties props;
    private final NotificationSettingsRepository notificationSettingsRepository;

    public AdminUserNotificationBuilder(BrevoTemplateProperties props, NotificationSettingsRepository notificationSettingsRepository) {
        this.props = props;
        this.notificationSettingsRepository = notificationSettingsRepository;
    }

    @Override
    public boolean supports(Class<?> payloadType) {
        return UserPayload.class.isAssignableFrom(payloadType);
    }

    @Override
    protected String extractEmail(UserPayload payload) {
        try {
            List<NotificationSettings> settingsList = notificationSettingsRepository.findAll();
            if (!settingsList.isEmpty()) {
                String adminEmail = settingsList.get(0).getAdminNotificationEmail();
                if (adminEmail != null && !adminEmail.isBlank()) {
                    return adminEmail;
                }
            }
        } catch (Exception e) {
            log.error("Failed to fetch local store settings for admin email", e);
        }
        return null;
    }

    @Override
    protected String extractPhone(UserPayload payload) {
        return null;
    }

    @Override
    protected String extractName(UserPayload payload) {
        return "Admin";
    }

    @Override
    protected UUID extractUserId(UserPayload payload) {
        return payload.getUserId();
    }

    @Override
    protected boolean applySpecifics(UserPayload payload, SendNotificationRequest request) {
        if (request.getRecipientEmail() == null) {
            return false;
        }

        request.getParams().put("adminName", "Admin");
        request.getParams().put("userEmail", payload.getEmail());
        request.getParams().put("userName", payload.getFirstName() + " " + payload.getLastName());
        request.getParams().put("userId", payload.getUserId());

        if (payload.getAction() == null) {
            return false;
        }

        switch (payload.getAction()) {
            case "REGISTERED":
                request.setTemplateId(props.getAdmin().getNewUserRegistration());
                request.setTitle("New User Registration!");
                break;
            default:
                return false;
        }
        
        if (request.getTemplateId() == null) {
            return false;
        }
        
        return true;
    }
}
