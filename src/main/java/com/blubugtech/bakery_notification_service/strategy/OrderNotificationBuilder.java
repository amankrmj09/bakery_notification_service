package com.blubugtech.bakery_notification_service.strategy;

import com.blubugtech.bakery_notification_service.dto.notification.SendNotificationRequest;
import com.blubugtech.bakery_notification_service.integration.brevo.BrevoTemplateProperties;
import com.blubugtech.common.contract.messaging.OrderPayload;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class OrderNotificationBuilder extends BaseNotificationBuilder<OrderPayload> {

    private final BrevoTemplateProperties props;

    public OrderNotificationBuilder(BrevoTemplateProperties props) {
        this.props = props;
    }

    @Override
    public boolean supports(Class<?> payloadType) {
        return OrderPayload.class.isAssignableFrom(payloadType);
    }

    @Override
    protected String extractEmail(OrderPayload payload) {
        return payload.getCustomerEmail();
    }

    @Override
    protected String extractName(OrderPayload payload) {
        return "Customer";
    }

    @Override
    protected UUID extractUserId(OrderPayload payload) {
        return payload.getUserId();
    }

    @Override
    protected boolean applySpecifics(OrderPayload payload, SendNotificationRequest request) {
        request.getParams().put("firstName", "Customer");
        request.getParams().put("orderId", payload.getOrderId());
        request.getParams().put("orderNumber", payload.getOrderNumber());
        request.getParams().put("totalAmount", payload.getTotalAmount());
        request.getParams().put("status", payload.getStatus());

        if (payload.getStatus() == null) {
            return false;
        }

        switch (payload.getStatus()) {
            case "CONFIRMED":
                request.setTemplateId(props.getOrder().getConfirmation());
                request.setTitle("Order Confirmed!");
                break;
            case "INVOICE_GENERATED":
                request.setTemplateId(props.getOrder().getInvoice());
                request.setTitle("Your Invoice is Ready");
                request.getParams().put("invoiceUrl", payload.getInvoiceUrl());
                break;
            case "DELIVERED":
                request.setTemplateId(props.getOrder().getDelivery());
                request.setTitle("Order Delivered");
                request.getParams().put("deliveryAddress", payload.getDeliveryAddress());
                break;
            default:
                return false;
        }
        return true;
    }
}
