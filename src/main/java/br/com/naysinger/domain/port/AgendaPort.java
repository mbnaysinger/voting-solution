package br.com.naysinger.domain.port;

import br.com.naysinger.domain.model.Agenda;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface AgendaPort {

    Mono<Agenda> save(Agenda agenda);

    Mono<Agenda> findById(String id);

    Mono<Agenda> findByAgendaId(String agendaId);

    Mono<Agenda> findBySessionId(String sessionId);

    Flux<Agenda> findAll();

    Flux<Agenda> findAgendasWithActiveSession();

    Mono<Agenda> addSession(String agendaId, LocalDateTime startTime, Integer durationMinutes);

    Mono<Agenda> closeSession(String sessionId);

    Mono<Agenda> closeAgenda(String agendaId);

    Mono<Agenda> addVote(String sessionId, String userId, String cpf, String voteType);
}
