package com.blubugtech.bakery_notification_service.integration.brevo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "brevo.templates")
public class BrevoTemplateProperties {
    private AuthProperties auth = new AuthProperties();
    private OrderProperties order = new OrderProperties();
    private PaymentProperties payment = new PaymentProperties();
    private FeedbackProperties feedback = new FeedbackProperties();
    private AdminProperties admin = new AdminProperties();

    @Setter
    @Getter
    public static class AuthProperties {
        private Long welcome;
        private Long otp;
        private Long passwordChange;
        private Long newSignIn;

    }

    @Setter
    @Getter
    public static class OrderProperties {
        private Long confirmation;
        private Long invoice;
        private Long delivery;
        private Long adminCancelled;

    }

    @Setter
    @Getter
    public static class PaymentProperties {
        private Long success;
        private Long refund;

    }

    @Setter
    @Getter
    public static class FeedbackProperties {
        private Long general;
        private Long review;
        private Long testimonial;
        private Long contactUs;

    }

    @Setter
    @Getter
    public static class AdminProperties {
        private Long newOrder;
        private Long paymentReceived;
        private Long userCancelledOrder;
        private Long newUserRegistration;

    }
}
