package br.com.naysinger.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {

    @Value("${server.connection-timeout:20000}")
    private int connectionTimeout;

    @Value("${voting-solution.cpf.validation.timeout:5000}")
    private int cpfValidationTimeout;

    @Value("${voting-solution.cpf.validation.retry.max-attempts:3}")
    private int maxRetryAttempts;

    @Value("${voting-solution.cpf.validation.retry.backoff.initial-interval:1000}")
    private int initialBackoffInterval;

    @Value("${voting-solution.cpf.validation.retry.backoff.multiplier:2.0}")
    private double backoffMultiplier;

    @Value("${voting-solution.cpf.validation.retry.backoff.max-interval:10000}")
    private int maxBackoffInterval;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(1024 * 1024); // 1MB
    }

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(cpfValidationTimeout))
                .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout);

        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.create()
                .responseTimeout(Duration.ofMillis(connectionTimeout))
                .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout);
    }
}
