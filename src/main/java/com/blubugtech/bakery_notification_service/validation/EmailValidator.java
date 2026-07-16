package com.blubugtech.bakery_notification_service.validation;

import org.springframework.stereotype.Component;

@Component
public class EmailValidator {
    public boolean isValid(String email) {
        return email != null && email.contains("@");
    }
}
