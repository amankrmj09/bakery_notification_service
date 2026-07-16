package com.blubugtech.bakery_notification_service.template;

public class PasswordResetTemplate {
    public static final String TEMPLATE_NAME = "PASSWORD_RESET";
    public static final String DEFAULT_SUBJECT = "Password Reset Request";
    
    public static String buildContent(String resetLink) {
        return "<html><body>" +
               "<h1>Password Reset</h1>" +
               "<p>Click <a href='" + resetLink + "'>here</a> to reset your password.</p>" +
               "</body></html>";
    }
}
