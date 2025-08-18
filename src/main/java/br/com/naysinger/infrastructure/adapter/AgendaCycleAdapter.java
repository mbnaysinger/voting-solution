package br.com.naysinger.infrastructure.adapter;

import br.com.naysinger.domain.port.AgendaPort;
import br.com.naysinger.domain.model.Agenda;
import br.com.naysinger.infrastructure.entity.AgendaCycleEntity;
import br.com.naysinger.infrastructure.mapper.AgendaCycleMapper;
import br.com.naysinger.infrastructure.repository.AgendaCycleRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AgendaCycleAdapter implements AgendaPort {
    
    private final AgendaCycleRepository repository;
    private final AgendaCycleMapper mapper;
    
    public AgendaCycleAdapter(AgendaCycleRepository repository, AgendaCycleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    
    @Override
    public Mono<Agenda> save(Agenda agenda) {
        AgendaCycleEntity entity = mapper.toEntity(agenda);
        return repository.save(entity)
                .map(mapper::toDomainModel);
    }
    
    @Override
    public Mono<Agenda> findById(String id) {
        return repository.findById(id)
                .map(mapper::toDomainModel);
    }
    
    @Override
    public Mono<Agenda> findByAgendaId(String agendaId) {
        return repository.findByAgendaId(agendaId)
                .map(mapper::toDomainModel);
    }
}
