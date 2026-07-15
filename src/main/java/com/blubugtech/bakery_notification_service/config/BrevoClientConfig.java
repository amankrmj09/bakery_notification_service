package com.blubugtech.bakery_notification_service.config;

import com.blubugtech.bakery_notification_service.client.BrevoEmailClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class BrevoClientConfig {

    @Bean
    public BrevoEmailClient brevoEmailClient(WebClient.Builder webClientBuilder) {
        WebClient webClient = webClientBuilder.build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient)).build();
        return factory.createClient(BrevoEmailClient.class);
    }
}
