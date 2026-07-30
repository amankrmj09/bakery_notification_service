package com.blubugtech.bakery_notification_service.strategy;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.integration.brevo.BrevoTemplateProperties;
import org.blubakery.common.messaging.contract.messaging.PaymentPayload;
import com.blubugtech.bakery_notification_service.entity.NotificationSettings;
import com.blubugtech.bakery_notification_service.repository.NotificationSettingsRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AdminPaymentNotificationBuilder extends BaseNotificationBuilder<PaymentPayload> {

    private final BrevoTemplateProperties props;
    private final NotificationSettingsRepository notificationSettingsRepository;

    public AdminPaymentNotificationBuilder(BrevoTemplateProperties props, NotificationSettingsRepository notificationSettingsRepository) {
        this.props = props;
        this.notificationSettingsRepository = notificationSettingsRepository;
    }

    @Override
    public boolean supports(Class<?> payloadType) {
        return PaymentPayload.class.isAssignableFrom(payloadType);
    }

    @Override
    protected String extractEmail(PaymentPayload payload) {
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
    protected String extractPhone(PaymentPayload payload) {
        return null;
    }

    @Override
    protected String extractName(PaymentPayload payload) {
        return "Admin";
    }

    @Override
    protected UUID extractUserId(PaymentPayload payload) {
        return payload.getUserId();
    }

    @Override
    protected boolean applySpecifics(PaymentPayload payload, SendNotificationRequest request) {
        if (request.getRecipientEmail() == null) {
            return false;
        }

        request.getParams().put("adminName", "Admin");
        request.getParams().put("paymentId", payload.getPaymentId());
        request.getParams().put("orderId", payload.getOrderId());
        request.getParams().put("amount", payload.getAmount());

        if (payload.getStatus() == null) {
            return false;
        }

        switch (payload.getStatus()) {
            case "SUCCESS":
                request.setTemplateId(props.getAdmin().getPaymentReceived());
                request.setTitle("Payment Received!");
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
