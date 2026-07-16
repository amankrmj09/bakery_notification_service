package com.blubugtech.bakery_notification_service.template;

public class OrderConfirmationTemplate {
    public static final String TEMPLATE_NAME = "ORDER_CONFIRMATION";
    public static final String DEFAULT_SUBJECT = "Your Order Confirmation";
    
    public static String buildContent(String userName, String orderId, String totalAmount) {
        return "<html><body>" +
               "<h1>Thank you, " + userName + "!</h1>" +
               "<p>Your order #" + orderId + " has been confirmed.</p>" +
               "<p>Total: " + totalAmount + "</p>" +
               "</body></html>";
    }
}
