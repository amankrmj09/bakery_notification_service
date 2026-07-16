package com.blubugtech.bakery_notification_service.template;

public class WelcomeTemplate {
    public static final String TEMPLATE_NAME = "WELCOME";
    public static final String DEFAULT_SUBJECT = "Welcome to Our Bakery!";
    
    public static String buildContent(String userName) {
        return "<html><body>" +
               "<h1>Welcome, " + userName + "!</h1>" +
               "<p>We're glad to have you.</p>" +
               "</body></html>";
    }
}
