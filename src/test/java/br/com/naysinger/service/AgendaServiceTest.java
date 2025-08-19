package br.com.naysinger.service;

import br.com.naysinger.common.enums.AgendaStatus;
import br.com.naysinger.domain.model.Agenda;
import br.com.naysinger.domain.model.Session;
import br.com.naysinger.domain.port.AgendaPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AgendaService Unit Tests")
public class AgendaServiceTest {

    @Mock
    private AgendaPort agendaPort;

    @InjectMocks
    private AgendaService agendaService;

    private Agenda agenda;

    @BeforeEach
    void setUp() {
        agenda = Agenda.builder()
                .agendaId("agendaId1")
                .title("Title")
                .description("Description")
                .createdBy("Creator")
                .createdAt(LocalDateTime.now())
                .status(AgendaStatus.ACTIVE)
                .sessions(new ArrayList<>())
                .build();
        agenda.addSession(LocalDateTime.now().minusMinutes(10), 10);
    }

    @Test
    @DisplayName("Should create agenda successfully")
    void shouldCreateAgendaSuccessfully() {
        when(agendaPort.save(any(Agenda.class))).thenReturn(Mono.just(agenda));

        StepVerifier.create(agendaService.createAgenda(agenda))
                .expectNext(agenda)
                .verifyComplete();

        verify(agendaPort).save(any(Agenda.class));
    }

    @Test
    @DisplayName("Should add session to existing agenda")
    void shouldAddSessionToExistingAgenda() {
        Agenda newAgenda = Agenda.builder()
                .agendaId("newAgendaId")
                .title("New Agenda")
                .description("Desc")
                .createdBy("Creator")
                .createdAt(LocalDateTime.now())
                .status(AgendaStatus.PENDING)
                .sessions(new ArrayList<>())
                .build();
        when(agendaPort.findByAgendaId(anyString())).thenReturn(Mono.just(newAgenda));
        when(agendaPort.addSession(anyString(), any(LocalDateTime.class), any(Integer.class)))
                .thenReturn(Mono.just(newAgenda.toBuilder().addSession(Session.createNew(LocalDateTime.now().plusMinutes(1), 5)).build()));

        StepVerifier.create(agendaService.addSession("newAgendaId", LocalDateTime.now().plusMinutes(1), 5))
                .expectNextMatches(a -> a.getSessions().size() == 1)
                .verifyComplete();

        verify(agendaPort).findByAgendaId(anyString());
        verify(agendaPort).addSession(anyString(), any(LocalDateTime.class), any(Integer.class));
    }
}
