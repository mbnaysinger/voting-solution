package br.com.naysinger.integration;

import br.com.naysinger.api.dto.AgendaRequestDTO;
import br.com.naysinger.api.dto.AgendaResponseDTO;
import br.com.naysinger.api.dto.vote.VoteRequestDTO;
import br.com.naysinger.api.dto.vote.VoteResultResponse;
import br.com.naysinger.common.enums.CpfStatus;
import br.com.naysinger.common.enums.SessionResult;
import br.com.naysinger.common.exception.CpfNotFoundException;
import br.com.naysinger.domain.port.CpfValidationPort;
import br.com.naysinger.infrastructure.repository.AgendaCycleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class AgendaControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AgendaCycleRepository repository;

    @MockitoBean
    private CpfValidationPort cpfValidationPort;

    @BeforeEach
    void setUp() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();
        Mockito.when(cpfValidationPort.check(Mockito.anyString())).thenReturn(Mono.just(CpfStatus.ABLE_TO_VOTE));
    }

    @AfterEach
    void tearDown() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();
    }

    @Test
    void shouldReturnNotFoundForNonExistentAgenda() {
        webTestClient.get()
                .uri("/api/v1/agenda/{agendaId}", "non-existent-id")
                .exchange()
                .expectStatus().isNotFound();
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

    @Test
    void shouldFindAgendaByAgendaId() {
        // Given
        createAgendaWithSession("Findable Agenda", 0)
                .as(StepVerifier::create)
                .assertNext(createdAgenda -> {
                    assertThat(createdAgenda).isNotNull();
                    String agendaId = createdAgenda.getAgendaId();

                    // When & Then
                    webTestClient.get()
                            .uri("/api/v1/agenda/{agendaId}", agendaId)
                            .exchange()
                            .expectStatus().isOk()
                            .expectBody(AgendaResponseDTO.class)
                            .value(response -> {
                                assertThat(response.getAgendaId()).isEqualTo(agendaId);
                                assertThat(response.getTitle()).isEqualTo("Findable Agenda");
                            });
                })
                .verifyComplete();
    }

    @Test
    void shouldAddVoteToSession() {
        // Given
        Mockito.when(cpfValidationPort.check(Mockito.anyString())).thenReturn(Mono.just(CpfStatus.ABLE_TO_VOTE));

        createAgendaWithSession("Vote Agenda", 1)
                .as(StepVerifier::create)
                .assertNext(createdAgenda -> {
                    String sessionId = createdAgenda.getSessions().getFirst().getSessionId();
                    VoteRequestDTO voteRequest = new VoteRequestDTO("user1", "11122233344", "YES");

                    // When & Then
                    webTestClient.post()
                            .uri("/api/v1/agenda/session/{sessionId}/vote", sessionId)
                            .bodyValue(voteRequest)
                            .exchange()
                            .expectStatus().isCreated()
                            .expectBody(String.class).isEqualTo("Voto computado com sucesso, obrigado.");
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnConflictWhenVotingTwiceWithSameCpf() {
        // Given
        Mockito.when(cpfValidationPort.check(Mockito.anyString())).thenReturn(Mono.just(CpfStatus.ABLE_TO_VOTE));

        createAgendaWithSession("Duplicate Vote Test", 1)
                .as(StepVerifier::create)
                .assertNext(createdAgenda -> {
                    String sessionId = createdAgenda.getSessions().getFirst().getSessionId();
                    VoteRequestDTO voteRequest = new VoteRequestDTO("user2", "55566677788", "NO");

                    webTestClient.post().uri("/api/v1/agenda/session/{sessionId}/vote", sessionId)
                            .bodyValue(voteRequest).exchange().expectStatus().isCreated();

                    // When & Then: votando novamente com o mesmo CPF
                    webTestClient.post()
                            .uri("/api/v1/agenda/session/{sessionId}/vote", sessionId)
                            .bodyValue(voteRequest) // Sending the same request again
                            .exchange()
                            .expectStatus().isEqualTo(409)
                            .expectHeader().contentType(MediaType.APPLICATION_JSON)
                            .expectBody()
                            .jsonPath("$.error").isEqualTo("CPF duplicado");
                })
                .verifyComplete();
    }

    @Test
    void shouldGetVoteResultsForClosedSession() {
        // Given
        Mockito.when(cpfValidationPort.check(Mockito.anyString())).thenReturn(Mono.just(CpfStatus.ABLE_TO_VOTE));

        createAgendaWithSession("Results Agenda", 1)
                .as(StepVerifier::create)
                .assertNext(createdAgenda -> {
                    String sessionId = createdAgenda.getSessions().getFirst().getSessionId();

                    // Cast votes
                    webTestClient.post().uri("/api/v1/agenda/session/{sessionId}/vote", sessionId)
                            .bodyValue(new VoteRequestDTO("user1", "11111111111", "YES")).exchange().expectStatus().isCreated();
                    webTestClient.post().uri("/api/v1/agenda/session/{sessionId}/vote", sessionId)
                            .bodyValue(new VoteRequestDTO("user2", "22222222222", "YES")).exchange().expectStatus().isCreated();
                    webTestClient.post().uri("/api/v1/agenda/session/{sessionId}/vote", sessionId)
                            .bodyValue(new VoteRequestDTO("user3", "33333333333", "NO")).exchange().expectStatus().isCreated();

                    // When: Close the session
                    webTestClient.post()
                            .uri("/api/v1/agenda/session/{sessionId}/close", sessionId)
                            .exchange()
                            .expectStatus().isOk();

                    // Then: Get results
                    webTestClient.get()
                            .uri("/api/v1/agenda/session/{sessionId}/result", sessionId)
                            .exchange()
                            .expectStatus().isOk()
                            .expectBody(VoteResultResponse.class)
                            .value(response -> {
                                assertThat(response.simVotes()).isEqualTo(2);
                                assertThat(response.naoVotes()).isEqualTo(1);
                                assertThat(response.totalVotes()).isEqualTo(3);
                                assertThat(response.winner()).isEqualTo(SessionResult.SIM);
                            });
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnNotFoundWhenCpfIsUnableToVote() {
        // Given
        Mockito.when(cpfValidationPort.check("12345678909")).thenReturn(Mono.error(new CpfNotFoundException("12345678909")));

        createAgendaWithSession("CPF Unable to Vote", 1)
                .as(StepVerifier::create)
                .assertNext(createdAgenda -> {
                    String sessionId = createdAgenda.getSessions().getFirst().getSessionId();
                    VoteRequestDTO voteRequest = new VoteRequestDTO("user1", "12345678909", "YES");

                    // When & Then
                    webTestClient.post()
                            .uri("/api/v1/agenda/session/{sessionId}/vote", sessionId)
                            .bodyValue(voteRequest)
                            .exchange()
                            .expectStatus().isNotFound()
                            .expectBody()
                            .jsonPath("$.error").isEqualTo("CPF não apto ou inválido");
                })
                .verifyComplete();
    }

    private Mono<AgendaResponseDTO> createAgendaWithSession(String title, int durationMinutes) {
        LocalDateTime startTime = LocalDateTime.now();
        Integer duration = durationMinutes > 0 ? durationMinutes : null;

        AgendaRequestDTO request = new AgendaRequestDTO(
                title,
                "Test session",
                "integration-test",
                startTime,
                duration
        );

        return webTestClient.post().uri("/api/v1/agenda")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(AgendaResponseDTO.class).getResponseBody()
                .next();
    }
}
