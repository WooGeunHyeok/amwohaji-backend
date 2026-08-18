package com.amwohaji.batch.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    private static final int BUFFER_SIZE = 10 * 1024 * 1024; // 10MB

    @Bean
    public WebClient webClient() {

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(60));

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(config -> config.defaultCodecs().maxInMemorySize(BUFFER_SIZE))
                .build();

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .build();
    }
}