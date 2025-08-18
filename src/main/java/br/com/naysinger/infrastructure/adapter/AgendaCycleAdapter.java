package br.com.naysinger.infrastructure.adapter;

import br.com.naysinger.domain.model.AgendaCycle;
import br.com.naysinger.domain.port.AgendaCyclePort;
import br.com.naysinger.infrastructure.entity.AgendaCycleEntity;
import br.com.naysinger.infrastructure.mapper.AgendaCycleMapper;
import br.com.naysinger.infrastructure.repository.AgendaCycleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

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
                // Criar nova sessão
                br.com.naysinger.infrastructure.entity.SessionEntity sessionEntity = createNewSessionEntity(agendaId, startTime, durationMinutes);
                agendaCycle.setSession(sessionEntity);
                
                return agendaCycleRepository.save(agendaCycle);
            })
            .map(agendaCycleMapper::toDomain);
    }

    @Override
    public Mono<AgendaCycle> closeSession(String sessionId) {
        return agendaCycleRepository.findBySessionId(sessionId)
            .flatMap(agendaCycle -> {
                if (agendaCycle.getSession() != null) {
                    agendaCycle.getSession().setStatus(br.com.naysinger.common.enums.SessionStatus.CLOSED);
                }
                return agendaCycleRepository.save(agendaCycle);
            })
            .map(agendaCycleMapper::toDomain);
    }

    @Override
    public Mono<AgendaCycle> addVote(String sessionId, String userId, String cpf, String voteType) {
        return agendaCycleRepository.findBySessionId(sessionId)
            .flatMap(agendaCycle -> {
                if (agendaCycle.getSession() != null) {
                    try {
                        br.com.naysinger.common.enums.VoteType vote = br.com.naysinger.common.enums.VoteType.valueOf(voteType.toUpperCase());
                        br.com.naysinger.infrastructure.entity.VoteEntity voteEntity = 
                            new br.com.naysinger.infrastructure.entity.VoteEntity(userId, cpf, vote);
                        agendaCycle.getSession().addVote(voteEntity);
                    } catch (IllegalArgumentException e) {
                        return Mono.error(new IllegalArgumentException("Tipo de voto inválido: " + voteType));
                    }
                }
                return agendaCycleRepository.save(agendaCycle);
            })
            .map(agendaCycleMapper::toDomain);
    }

    private br.com.naysinger.infrastructure.entity.SessionEntity createNewSessionEntity(String agendaId, LocalDateTime startTime, Integer durationMinutes) {
        String sessionId = java.util.UUID.randomUUID().toString();
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);
        
        return new br.com.naysinger.infrastructure.entity.SessionEntity(
            sessionId,
            startTime,
            endTime,
            br.com.naysinger.common.enums.SessionStatus.OPEN,
            new java.util.ArrayList<>()
        );
    }
}
