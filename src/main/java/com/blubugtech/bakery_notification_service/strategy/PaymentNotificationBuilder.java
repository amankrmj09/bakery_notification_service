package com.blubugtech.bakery_notification_service.strategy;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.integration.brevo.BrevoTemplateProperties;
import org.blubakery.common.messaging.contract.messaging.PaymentPayload;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class PaymentNotificationBuilder extends BaseNotificationBuilder<PaymentPayload> {

    private final BrevoTemplateProperties props;

    public PaymentNotificationBuilder(BrevoTemplateProperties props) {
        this.props = props;
    }

    @Override
    public boolean supports(Class<?> payloadType) {
        return PaymentPayload.class.isAssignableFrom(payloadType);
    }

    @Override
    protected String extractEmail(PaymentPayload payload) {
        return payload.getCustomerEmail();
    }

    @Override
    protected String extractName(PaymentPayload payload) {
        return "Customer";
    }

    @Override
    protected UUID extractUserId(PaymentPayload payload) {
        return payload.getUserId();
    }

    @Override
    protected String extractPhone(PaymentPayload payload) {
        return payload.getCustomerPhone();
    }

    @Override
    protected boolean applySpecifics(PaymentPayload payload, SendNotificationRequest request) {
        request.getParams().put("firstName", "Customer");
        request.getParams().put("orderId", payload.getOrderId());
        request.getParams().put("paymentId", payload.getPaymentId());

        request.getChannels().add(com.blubugtech.bakery_notification_service.enums.NotificationChannel.EMAIL);
        if (payload.getCustomerPhone() != null && !payload.getCustomerPhone().trim().isEmpty()) {
            request.getChannels().add(com.blubugtech.bakery_notification_service.enums.NotificationChannel.SMS);
        }

        if (payload.getStatus() == null) {
            return false;
        }

        switch (payload.getStatus()) {
            case "SUCCESS":
                request.setTemplateId(props.getPayment().getSuccess());
                request.setTitle("Payment Success");
                request.getParams().put("amount", payload.getAmount());
                request.getParams().put("status", payload.getStatus());
                request.setSmsTag("PAYMENT_SUCCESS");
                request.setSmsContent("Payment of " + payload.getAmount() + " was successful for Order: " + payload.getOrderId());
                break;
            case "REFUNDED":
                request.setTemplateId(props.getPayment().getRefund());
                request.setTitle("Refund Processed");
                request.getParams().put("refundAmount", payload.getRefundAmount());
                request.getParams().put("refundReason", payload.getRefundReason());
                request.setSmsTag("PAYMENT_REFUND");
                request.setSmsContent("Refund of " + payload.getRefundAmount() + " processed for Order: " + payload.getOrderId());
                break;
            default:
                return false;
        }
        return true;
    }
}
