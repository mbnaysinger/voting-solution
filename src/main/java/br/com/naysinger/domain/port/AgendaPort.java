package br.com.naysinger.domain.port;

import br.com.naysinger.domain.model.Agenda;
import reactor.core.publisher.Mono;

public interface AgendaPort {
    
    /**
     * Salva uma nova pauta
     */
    Mono<Agenda> save(Agenda agenda);
    
    /**
     * Busca uma pauta por ID
     */
    Mono<Agenda> findById(String id);
    
    /**
     * Busca uma pauta por agendaId
     */
    Mono<Agenda> findByAgendaId(String agendaId);
}
