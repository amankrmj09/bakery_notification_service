package com.blubugtech.bakery_notification_service.integration.brevo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("https://api.brevo.com/v3")
public interface BrevoSmsClient {

    @PostExchange("/transactionalSMS/send")
    ResponseEntity<BrevoSmsResponse> sendSms(
            @RequestHeader("api-key") String apiKey,
            @RequestBody BrevoSmsRequest request
    );
}
