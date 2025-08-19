package br.com.naysinger.integration;

import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureWebTestClient(timeout = "60s")
public abstract class AbstractIntegrationTest {

    @Autowired
    protected WebTestClient webTestClient;

    @Container
    static GenericContainer<?> mongoDBContainer = new GenericContainer<>(DockerImageName.parse("mongo:6.0"))
            .withEnv("MONGO_INITDB_ROOT_USERNAME", "admin")
            .withEnv("MONGO_INITDB_ROOT_PASSWORD", "voting123")
            .withExposedPorts(27017)
            .waitingFor(
                    Wait.forLogMessage(".*Waiting for connections.*\\n", 1)
                            .withStartupTimeout(Duration.ofSeconds(120))
            )
            .withReuse(false); // false para evitar conflitos

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
        registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);
        registry.add("spring.data.mongodb.database", () -> "voting-solution-test-" + System.currentTimeMillis());
        registry.add("spring.data.mongodb.username", () -> "admin");
        registry.add("spring.data.mongodb.password", () -> "voting123");
        registry.add("spring.data.mongodb.authentication-database", () -> "admin");
        // Configurações de timeout mais agressivas
        registry.add("spring.data.mongodb.options.serverSelectionTimeoutMS", () -> 10000);
        registry.add("spring.data.mongodb.options.connectTimeoutMS", () -> 10000);
        registry.add("spring.data.mongodb.options.socketTimeoutMS", () -> 10000);
    }
}