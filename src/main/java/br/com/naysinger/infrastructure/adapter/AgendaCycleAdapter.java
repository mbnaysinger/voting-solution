package br.com.naysinger.infrastructure.adapter;

import br.com.naysinger.domain.model.AgendaCycle;
import br.com.naysinger.domain.port.AgendaCyclePort;
import br.com.naysinger.infrastructure.entity.AgendaCycleEntity;
import br.com.naysinger.infrastructure.mapper.AgendaCycleMapper;
import br.com.naysinger.infrastructure.repository.AgendaCycleRepository;
import br.com.naysinger.common.enums.SessionStatus;
import br.com.naysinger.common.enums.VoteType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import br.com.naysinger.infrastructure.entity.SessionEntity;
import br.com.naysinger.infrastructure.entity.VoteEntity;

@Component
public class AgendaCycleAdapter implements AgendaCyclePort {
    
    private static final Logger logger = LoggerFactory.getLogger(AgendaCycleAdapter.class);
    
    private final AgendaCycleRepository agendaCycleRepository;
    private final AgendaCycleMapper agendaCycleMapper;
    
    public AgendaCycleAdapter(AgendaCycleRepository agendaCycleRepository, AgendaCycleMapper agendaCycleMapper) {
        this.agendaCycleRepository = agendaCycleRepository;
        this.agendaCycleMapper = agendaCycleMapper;
    }
    
    @Override
    public Mono<AgendaCycle> save(AgendaCycle agendaCycle) {
        AgendaCycleEntity entity = agendaCycleMapper.toEntity(agendaCycle);
        return agendaCycleRepository.save(entity)
            .map(agendaCycleMapper::toDomain);
    }
    
    @Override
    public Mono<AgendaCycle> findById(String id) {
        return agendaCycleRepository.findById(id)
            .map(agendaCycleMapper::toDomain);
    }
    
    @Override
    public Mono<AgendaCycle> findByAgendaId(String agendaId) {
        return agendaCycleRepository.findByAgendaId(agendaId)
            .map(agendaCycleMapper::toDomain);
    }
    
    @Override
    public Mono<AgendaCycle> findBySessionId(String sessionId) {
        return agendaCycleRepository.findBySessionId(sessionId)
            .map(agendaCycleMapper::toDomain);
    }
    
    @Override
    public Flux<AgendaCycle> findAll() {
        return agendaCycleRepository.findAll()
            .map(agendaCycleMapper::toDomain);
    }
    
    @Override
    public Flux<AgendaCycle> findAgendasWithActiveSession() {
        return agendaCycleRepository.findAgendasWithActiveSession()
            .map(agendaCycleMapper::toDomain);
    }
    
    @Override
    public Mono<AgendaCycle> update(AgendaCycle agendaCycle) {
        AgendaCycleEntity entity = agendaCycleMapper.toEntity(agendaCycle);
        return agendaCycleRepository.save(entity)
            .map(agendaCycleMapper::toDomain);
    }
    
    @Override
    public Mono<AgendaCycle> addSession(String agendaId, LocalDateTime startTime, Integer durationMinutes) {
        return agendaCycleRepository.findByAgendaId(agendaId)
            .flatMap(agendaCycle -> {
                SessionEntity sessionEntity = createNewSessionEntity(startTime, durationMinutes);
                
                // Inicializar lista de sessões se não existir
                if (agendaCycle.getSessions() == null) {
                    agendaCycle.setSessions(new ArrayList<>());
                }
                
                // Adicionar nova sessão à lista (não substituir)
                agendaCycle.getSessions().add(sessionEntity);
                
                return agendaCycleRepository.save(agendaCycle);
            })
            .map(agendaCycleMapper::toDomain);
    }
    
    @Override
    public Mono<AgendaCycle> closeSession(String sessionId) {
        return agendaCycleRepository.findBySessionId(sessionId)
            .flatMap(agendaCycle -> {
                if (agendaCycle.getSessions() != null) {
                    agendaCycle.getSessions().stream()
                        .filter(session -> session.getSessionId().equals(sessionId))
                        .findFirst()
                        .ifPresent(session -> session.setStatus(SessionStatus.CLOSED));
                }
                return agendaCycleRepository.save(agendaCycle);
            })
            .map(agendaCycleMapper::toDomain);
    }
    
    @Override
    public Mono<AgendaCycle> addVote(String sessionId, String userId, String cpf, String voteType) {
        return agendaCycleRepository.findBySessionId(sessionId)
            .flatMap(agendaCycle -> {
                if (agendaCycle.getSessions() != null) {
                    try {
                        VoteType vote = VoteType.valueOf(voteType.toUpperCase());
                        VoteEntity voteEntity = new VoteEntity(userId, cpf, vote);
                        
                        agendaCycle.getSessions().stream()
                            .filter(session -> session.getSessionId().equals(sessionId))
                            .findFirst()
                            .ifPresent(session -> session.addVote(voteEntity));
                            
                    } catch (IllegalArgumentException e) {
                        return Mono.error(new IllegalArgumentException("Tipo de voto inválido: " + voteType));
                    }
                }
                return agendaCycleRepository.save(agendaCycle);
            })
            .map(agendaCycleMapper::toDomain);
    }
    
    private SessionEntity createNewSessionEntity(LocalDateTime startTime, Integer durationMinutes) {
        return SessionEntity.createNew(startTime, durationMinutes);
    }
}
