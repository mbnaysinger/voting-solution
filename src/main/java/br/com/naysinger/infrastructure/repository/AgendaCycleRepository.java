package br.com.naysinger.infrastructure.repository;

import br.com.naysinger.infrastructure.entity.AgendaCycleEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Repository
public interface AgendaCycleRepository extends ReactiveMongoRepository<AgendaCycleEntity, String> {

    Mono<AgendaCycleEntity> findByAgendaId(String agendaId);

    @Query("{ 'sessions.session_id': ?0}")
    Mono<AgendaCycleEntity> findBySessionId(String sessionId);

    @Query("{ 'agenda_id': ?0, 'sessions.session_id': ?1 }")
    Mono<AgendaCycleEntity> findByAgendaIdAndSessionId(String agendaId, String sessionId);

    @Query("{ 'sessions': { $exists: true, $ne: null } }")
    Flux<AgendaCycleEntity> findAgendasWithSession();

    @Query("{ 'sessions.status': 'OPEN' }")
    Flux<AgendaCycleEntity> findAgendasWithActiveSession();

    @Query("{ 'agendaId': ?0, 'session': { $exists: true, $ne: null } }")
    Mono<AgendaCycleEntity> findByAgendaIdWithSession(String agendaId);
}
