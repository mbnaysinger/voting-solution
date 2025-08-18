package br.com.naysinger.domain.service;

import br.com.naysinger.domain.port.AgendaPort;
import br.com.naysinger.domain.model.Agenda;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AgendaService {
    
    private final AgendaPort agendaPort;
    
    public AgendaService(AgendaPort agendaPort) {
        this.agendaPort = agendaPort;
    }
    
    /**
     * Cria uma nova pauta
     */
    public Mono<Agenda> createAgenda(Agenda agenda) {
        // Validações de negócio podem ser adicionadas aqui
        if (agenda.getTitle() == null || agenda.getTitle().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("O título da pauta é obrigatório"));
        }
        
        if (agenda.getDescription() == null || agenda.getDescription().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("A descrição da pauta é obrigatória"));
        }
        
        return agendaPort.save(agenda);
    }
    
    /**
     * Busca uma pauta por ID
     */
    public Mono<Agenda> findAgendaById(String id) {
        return agendaPort.findById(id);
    }
    
    /**
     * Busca uma pauta por agendaId
     */
    public Mono<Agenda> findAgendaByAgendaId(String agendaId) {
        return agendaPort.findByAgendaId(agendaId);
    }
}
