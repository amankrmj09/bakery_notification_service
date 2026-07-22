package com.blubugtech.bakery_notification_service.strategy;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.integration.brevo.BrevoTemplateProperties;
import com.blubugtech.common.contract.messaging.UserPayload;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class UserNotificationBuilder extends BaseNotificationBuilder<UserPayload> {

    private final BrevoTemplateProperties props;

    public UserNotificationBuilder(BrevoTemplateProperties props) {
        this.props = props;
    }

    @Override
    public boolean supports(Class<?> payloadType) {
        return UserPayload.class.isAssignableFrom(payloadType);
    }

    @Override
    protected String extractEmail(UserPayload payload) {
        return payload.getEmail();
    }

    @Override
    protected String extractName(UserPayload payload) {
        return payload.getFirstName() + " " + (payload.getLastName() != null ? payload.getLastName() : "");
    }

    @Override
    protected UUID extractUserId(UserPayload payload) {
        return payload.getUserId();
    }

    @Override
    protected boolean applySpecifics(UserPayload payload, SendNotificationRequest request) {
        request.getParams().put("firstName", payload.getFirstName());

        if (payload.getAction() == null) {
            return false;
        }

        switch (payload.getAction()) {
            case "REGISTERED":
                request.setTemplateId(props.getAuth().getWelcome());
                request.setTitle("Welcome to Blu's Bakery");
                request.getParams().put("lastName", payload.getLastName());
                break;
            case "PASSWORD_CHANGED":
                request.setTemplateId(props.getAuth().getPasswordChange());
                request.setTitle("Security Alert - Password Changed");
                break;
            case "OTP_REQUESTED":
                request.setTemplateId(props.getAuth().getOtp());
                request.setTitle("Your OTP Code");
                request.getParams().put("otpCode", payload.getOtpCode());
                request.getParams().put("expiryMinutes", payload.getExpiryMinutes());
                break;
            case "NEW_SIGN_IN":
                request.setTemplateId(props.getAuth().getNewSignIn());
                request.setTitle("New Sign-In Detected");
                request.getParams().put("ipAddress", payload.getIpAddress());
                request.getParams().put("location", payload.getLocation());
                request.getParams().put("time", payload.getTimestamp());
                break;
            default:
                return false;
        }
        return true;
    }
}
