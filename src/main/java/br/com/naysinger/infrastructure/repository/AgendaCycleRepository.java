package br.com.naysinger.infrastructure.repository;

import br.com.naysinger.common.enums.AgendaStatus;
import br.com.naysinger.common.enums.SessionStatus;
import br.com.naysinger.infrastructure.entity.AgendaCycleEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AgendaCycleRepository extends ReactiveMongoRepository<AgendaCycleEntity, String> {

    Mono<AgendaCycleEntity> findByAgendaId(String agendaId);

    Flux<AgendaCycleEntity> findByStatus(AgendaStatus status);

    @Query("{'session.status': ?0}")
    Flux<AgendaCycleEntity> findBySessionStatus(SessionStatus status);

}
