package com.blubugtech.bakery_notification_service.strategy;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.integration.brevo.BrevoTemplateProperties;
import com.blubugtech.common.contract.messaging.FeedbackPayload;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class FeedbackNotificationBuilder extends BaseNotificationBuilder<FeedbackPayload> {

    private final BrevoTemplateProperties props;

    public FeedbackNotificationBuilder(BrevoTemplateProperties props) {
        this.props = props;
    }

    @Override
    public boolean supports(Class<?> payloadType) {
        return FeedbackPayload.class.isAssignableFrom(payloadType);
    }

    @Override
    protected String extractEmail(FeedbackPayload payload) {
        return payload.getCustomerEmail();
    }

    @Override
    protected String extractName(FeedbackPayload payload) {
        return payload.getFirstName() != null ? payload.getFirstName() : "Customer";
    }

    @Override
    protected UUID extractUserId(FeedbackPayload payload) {
        return payload.getUserId();
    }

    @Override
    protected boolean applySpecifics(FeedbackPayload payload, SendNotificationRequest request) {
        request.getParams().put("firstName", payload.getFirstName());

        if (payload.getType() == null) {
            return false;
        }

        switch (payload.getType()) {
            case "GENERAL":
                request.setTemplateId(props.getFeedback().getGeneral());
                request.setTitle("Feedback Received");
                request.getParams().put("ticketId", payload.getTicketId());
                break;
            case "PRODUCT_REVIEW":
                request.setTemplateId(props.getFeedback().getReview());
                request.setTitle("Thank You For Your Review");
                request.getParams().put("productName", payload.getProductName());
                request.getParams().put("rating", payload.getRating());
                break;
            default:
                return false;
        }
        return true;
    }
}
