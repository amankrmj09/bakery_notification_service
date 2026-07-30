package com.blubugtech.bakery_notification_service.strategy;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.integration.brevo.BrevoTemplateProperties;
import org.blubakery.common.messaging.contract.messaging.OrderPayload;
import com.blubugtech.bakery_notification_service.entity.NotificationSettings;
import com.blubugtech.bakery_notification_service.repository.NotificationSettingsRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AdminOrderNotificationBuilder extends BaseNotificationBuilder<OrderPayload> {

    private final BrevoTemplateProperties props;
    private final NotificationSettingsRepository notificationSettingsRepository;

    public AdminOrderNotificationBuilder(BrevoTemplateProperties props, NotificationSettingsRepository notificationSettingsRepository) {
        this.props = props;
        this.notificationSettingsRepository = notificationSettingsRepository;
    }

    @Override
    public boolean supports(Class<?> payloadType) {
        return OrderPayload.class.isAssignableFrom(payloadType);
    }

    @Override
    protected String extractEmail(OrderPayload payload) {
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
    protected String extractPhone(OrderPayload payload) {
        return null;
    }

    @Override
    protected String extractName(OrderPayload payload) {
        return "Admin";
    }

    @Override
    protected UUID extractUserId(OrderPayload payload) {
        return payload.getUserId();
    }

    @Override
    protected boolean applySpecifics(OrderPayload payload, SendNotificationRequest request) {
        if (request.getRecipientEmail() == null) {
            return false;
        }

        request.getParams().put("adminName", "Admin");
        request.getParams().put("orderId", payload.getOrderId());
        request.getParams().put("orderNumber", payload.getOrderNumber());
        request.getParams().put("totalAmount", payload.getTotalAmount());
        
        request.getParams().put("customerEmail", payload.getCustomerEmail());
        request.getParams().put("customerName", "Customer");

        if (payload.getStatus() == null) {
            return false;
        }

        switch (payload.getStatus()) {
            case "CONFIRMED":
                request.setTemplateId(props.getAdmin().getNewOrder());
                request.setTitle("New Order Received!");
                break;
            case "CANCELLED":
                if (Boolean.TRUE.equals(payload.getCancelledByAdmin())) {
                    return false;
                }
                request.setTemplateId(props.getAdmin().getUserCancelledOrder());
                request.setTitle("User Cancelled Order");
                request.getParams().put("cancellationReason", payload.getCancellationReason() != null ? payload.getCancellationReason() : "User decision");
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
