package com.blubugtech.bakery_notification_service.integration.brevo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "brevo.templates")
public class BrevoTemplateProperties {
    private AuthProperties auth = new AuthProperties();
    private OrderProperties order = new OrderProperties();
    private PaymentProperties payment = new PaymentProperties();
    private FeedbackProperties feedback = new FeedbackProperties();

    public AuthProperties getAuth() { return auth; }
    public void setAuth(AuthProperties auth) { this.auth = auth; }
    
    public OrderProperties getOrder() { return order; }
    public void setOrder(OrderProperties order) { this.order = order; }
    
    public PaymentProperties getPayment() { return payment; }
    public void setPayment(PaymentProperties payment) { this.payment = payment; }
    
    public FeedbackProperties getFeedback() { return feedback; }
    public void setFeedback(FeedbackProperties feedback) { this.feedback = feedback; }

    public static class AuthProperties {
        private Long welcome;
        private Long otp;
        private Long passwordChange;
        private Long newSignIn;

        public Long getWelcome() { return welcome; }
        public void setWelcome(Long welcome) { this.welcome = welcome; }
        public Long getOtp() { return otp; }
        public void setOtp(Long otp) { this.otp = otp; }
        public Long getPasswordChange() { return passwordChange; }
        public void setPasswordChange(Long passwordChange) { this.passwordChange = passwordChange; }
        public Long getNewSignIn() { return newSignIn; }
        public void setNewSignIn(Long newSignIn) { this.newSignIn = newSignIn; }
    }

    public static class OrderProperties {
        private Long confirmation;
        private Long invoice;
        private Long delivery;

        public Long getConfirmation() { return confirmation; }
        public void setConfirmation(Long confirmation) { this.confirmation = confirmation; }
        public Long getInvoice() { return invoice; }
        public void setInvoice(Long invoice) { this.invoice = invoice; }
        public Long getDelivery() { return delivery; }
        public void setDelivery(Long delivery) { this.delivery = delivery; }
    }

    public static class PaymentProperties {
        private Long success;
        private Long refund;

        public Long getSuccess() { return success; }
        public void setSuccess(Long success) { this.success = success; }
        public Long getRefund() { return refund; }
        public void setRefund(Long refund) { this.refund = refund; }
    }

    public static class FeedbackProperties {
        private Long general;
        private Long review;

        public Long getGeneral() { return general; }
        public void setGeneral(Long general) { this.general = general; }
        public Long getReview() { return review; }
        public void setReview(Long review) { this.review = review; }
    }
}
