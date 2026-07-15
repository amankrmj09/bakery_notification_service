package com.blubugtech.bakery_notification_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "brevo.templates")
public class BrevoTemplateProperties {
    private Long transaction;
    private Long order;
    private Long payment;
    private Long welcome;
    private Long passwordChange;

    public Long getTransaction() {
        return transaction;
    }

    public void setTransaction(Long transaction) {
        this.transaction = transaction;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public Long getPayment() {
        return payment;
    }

    public void setPayment(Long payment) {
        this.payment = payment;
    }

    public Long getWelcome() {
        return welcome;
    }

    public void setWelcome(Long welcome) {
        this.welcome = welcome;
    }

    public Long getPasswordChange() {
        return passwordChange;
    }

    public void setPasswordChange(Long passwordChange) {
        this.passwordChange = passwordChange;
    }
}
