package br.com.naysinger.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public Mono<Map<String, Object>> health() {
        return Mono.just(Map.of(
                "status", "UP",
                "service", "Voting Solution API",
                "version", "1.0.0",
                "apiVersions", Map.of(
                        "v1", "/api/v1",
                        "swagger", "/swagger-ui.html"
                ),
                "timestamp", System.currentTimeMillis()
        ));
    }
}
