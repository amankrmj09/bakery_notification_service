package com.blubugtech.bakery_notification_service.integration.brevo;

import com.blubugtech.bakery_notification_service.dto.email.BrevoEmailResponse;
import com.blubugtech.bakery_notification_service.dto.email.BrevoEmailRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("https://api.brevo.com/v3")
public interface BrevoEmailClient {

    @PostExchange(value = "/smtp/email", accept = "application/json", contentType = "application/json")
    ResponseEntity<BrevoEmailResponse> sendTemplateEmail(
            @RequestHeader("api-key") String apiKey,
            @RequestBody BrevoEmailRequest request
    );
}
