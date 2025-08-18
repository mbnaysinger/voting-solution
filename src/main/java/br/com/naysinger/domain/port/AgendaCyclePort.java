package br.com.naysinger.domain.port;

import br.com.naysinger.domain.model.AgendaCycle;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface AgendaCyclePort {
    
    /**
     * Salva uma nova agenda com ou sem sessão
     */
    Mono<AgendaCycle> save(AgendaCycle agendaCycle);
    
    /**
     * Busca uma agenda por ID
     */
    Mono<AgendaCycle> findById(String id);
    
    /**
     * Busca uma agenda por agendaId
     */
    Mono<AgendaCycle> findByAgendaId(String agendaId);
    
    /**
     * Busca uma agenda por sessionId
     */
    Mono<AgendaCycle> findBySessionId(String sessionId);
    
    /**
     * Busca todas as agendas
     */
    Flux<AgendaCycle> findAll();
    
    /**
     * Busca agendas com sessões ativas
     */
    Flux<AgendaCycle> findAgendasWithActiveSession();
    
    /**
     * Atualiza uma agenda
     */
    Mono<AgendaCycle> update(AgendaCycle agendaCycle);
    
    /**
     * Adiciona uma sessão a uma agenda existente
     */
    Mono<AgendaCycle> addSession(String agendaId, LocalDateTime startTime, Integer durationMinutes);
    
    /**
     * Fecha uma sessão
     */
    Mono<AgendaCycle> closeSession(String sessionId);
    
    /**
     * Adiciona um voto a uma sessão
     */
    Mono<AgendaCycle> addVote(String sessionId, String userId, String cpf, String voteType);
}
