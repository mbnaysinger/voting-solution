package br.com.naysinger.domain.port;

import br.com.naysinger.domain.model.AgendaCycle;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface AgendaCyclePort {

    Mono<AgendaCycle> save(AgendaCycle agendaCycle);

    Mono<AgendaCycle> findById(String id);

    Mono<AgendaCycle> findByAgendaId(String agendaId);

    Mono<AgendaCycle> findBySessionId(String sessionId);

    Flux<AgendaCycle> findAll();

    Flux<AgendaCycle> findAgendasWithActiveSession();

    Mono<AgendaCycle> update(AgendaCycle agendaCycle);

    Mono<AgendaCycle> addSession(String agendaId, LocalDateTime startTime, Integer durationMinutes);

    Mono<AgendaCycle> closeSession(String sessionId);

    Mono<AgendaCycle> addVote(String sessionId, String userId, String cpf, String voteType);
}
