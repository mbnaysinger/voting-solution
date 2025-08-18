package br.com.naysinger.integration;

import br.com.naysinger.api.dto.AgendaRequestDTO;
import br.com.naysinger.api.dto.AgendaResponseDTO;
import br.com.naysinger.infrastructure.repository.AgendaCycleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class AgendaControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AgendaCycleRepository repository;

    @AfterEach
    void tearDown() {
        repository.deleteAll().block();
    }

    @Test
    void shouldCreateAgendaSuccessfully() {
        // Given
        AgendaRequestDTO request = new AgendaRequestDTO(
                "New Agenda Title",
                "Some description",
                "test-creator",
                LocalDateTime.now().plusMinutes(5),
                10
        );

        // When & Then
        webTestClient.post()
                .uri("/api/v1/agenda")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AgendaResponseDTO.class)
                .value(response -> {
                    assertThat(response.getTitle()).isEqualTo("New Agenda Title");
                    assertThat(response.getCreatedBy()).isEqualTo("test-creator");
                    assertThat(response.getSessions()).hasSize(1);
                });
    }
}
