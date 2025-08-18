package br.com.naysinger.infrastructure.adapter;

import br.com.naysinger.domain.model.Agenda;
import br.com.naysinger.domain.port.AgendaPort;
import br.com.naysinger.infrastructure.entity.AgendaCycleEntity;
import br.com.naysinger.infrastructure.mapper.AgendaCycleMapper;
import br.com.naysinger.infrastructure.repository.AgendaCycleRepository;
import br.com.naysinger.common.enums.SessionStatus;
import br.com.naysinger.common.enums.AgendaStatus;
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
public class AgendaCycleAdapter implements AgendaPort {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AgendaCycleAdapter.class);
    
    private final AgendaCycleRepository agendaCycleRepository;
    private final AgendaCycleMapper agendaCycleMapper;
    
    public AgendaCycleAdapter(AgendaCycleRepository agendaCycleRepository, AgendaCycleMapper agendaCycleMapper) {
        this.agendaCycleRepository = agendaCycleRepository;
        this.agendaCycleMapper = agendaCycleMapper;
    }
    
    @Override
    public Mono<Agenda> save(Agenda agenda) {
        LOGGER.debug("[adapter.save] Salvando agenda. agendaId={}", agenda.getAgendaId());
        AgendaCycleEntity entity = agendaCycleMapper.toEntity(agenda);
        return agendaCycleRepository.save(entity)
            .doOnSuccess(e -> LOGGER.info("[adapter.save] Agenda salva. agendaId={}", e.getAgendaId()))
            .doOnError(err -> LOGGER.error("[adapter.save] Falha ao salvar agenda. agendaId={}", agenda.getAgendaId(), err))
            .map(agendaCycleMapper::toDomain);
    }
    
    @Override
    public Mono<Agenda> findById(String id) {
        LOGGER.debug("[adapter.findById] id={}", id);
        return agendaCycleRepository.findById(id)
            .map(agendaCycleMapper::toDomain);
    }
    
    @Override
    public Mono<Agenda> findByAgendaId(String agendaId) {
        LOGGER.debug("[adapter.findByAgendaId] agendaId={}", agendaId);
        return agendaCycleRepository.findByAgendaId(agendaId)
            .map(agendaCycleMapper::toDomain);
    }
    
    @Override
    public Mono<Agenda> findBySessionId(String sessionId) {
        LOGGER.debug("[adapter.findBySessionId] sessionId={}", sessionId);
        return agendaCycleRepository.findBySessionId(sessionId)
            .map(agendaCycleMapper::toDomain);
    }
    
    @Override
    public Flux<Agenda> findAll() {
        LOGGER.debug("[adapter.findAll] Listando agendas");
        return agendaCycleRepository.findAll()
            .map(agendaCycleMapper::toDomain);
    }
    
    @Override
    public Flux<Agenda> findAgendasWithActiveSession() {
        LOGGER.debug("[adapter.findAgendasWithActiveSession]");
        return agendaCycleRepository.findAgendasWithActiveSession()
            .map(agendaCycleMapper::toDomain);
    }
    
    @Override
    public Mono<Agenda> addSession(String agendaId, LocalDateTime startTime, Integer durationMinutes) {
        LOGGER.info("[adapter.addSession] agendaId={}, startTime={}, durationMinutes={}", agendaId, startTime, durationMinutes);
        return agendaCycleRepository.findByAgendaId(agendaId)
            .flatMap(agendaCycle -> {
                SessionEntity sessionEntity = createNewSessionEntity(startTime, durationMinutes);
                if (agendaCycle.getSessions() == null) {
                    agendaCycle.setSessions(new ArrayList<>());
                }
                agendaCycle.getSessions().add(sessionEntity);
                return agendaCycleRepository.save(agendaCycle)
                    .doOnSuccess(e -> LOGGER.info("[adapter.addSession] Sessão adicionada. agendaId={}, sessionsCount={}", agendaId, e.getSessions() != null ? e.getSessions().size() : 0));
            })
            .doOnError(err -> LOGGER.error("[adapter.addSession] Erro ao adicionar sessão. agendaId={}", agendaId, err))
            .map(agendaCycleMapper::toDomain);
    }
    
    @Override
    public Mono<Agenda> closeSession(String sessionId) {
        LOGGER.info("[adapter.closeSession] sessionId={}", sessionId);
        return agendaCycleRepository.findBySessionId(sessionId)
            .flatMap(agendaCycle -> {
                if (agendaCycle.getSessions() != null) {
                    agendaCycle.getSessions().stream()
                        .filter(session -> session.getSessionId().equals(sessionId))
                        .findFirst()
                        .ifPresent(session -> session.setStatus(SessionStatus.CLOSED));
                }
                return agendaCycleRepository.save(agendaCycle)
                    .doOnSuccess(e -> LOGGER.info("[adapter.closeSession] Sessão fechada. sessionId={}", sessionId));
            })
            .doOnError(err -> LOGGER.error("[adapter.closeSession] Erro ao fechar sessão. sessionId={}", sessionId, err))
            .map(agendaCycleMapper::toDomain);
    }
    
    @Override
    public Mono<Agenda> closeAgenda(String agendaId) {
        LOGGER.info("[adapter.closeAgenda] agendaId={}", agendaId);
        return agendaCycleRepository.findByAgendaId(agendaId)
            .flatMap(agendaCycle -> {
                agendaCycle.setStatus(AgendaStatus.CLOSED);
                if (agendaCycle.getSessions() != null) {
                    agendaCycle.getSessions().stream()
                        .filter(session -> session.getStatus() == SessionStatus.OPEN)
                        .forEach(session -> session.setStatus(SessionStatus.CLOSED));
                }
                return agendaCycleRepository.save(agendaCycle)
                    .doOnSuccess(e -> LOGGER.info("[adapter.closeAgenda] Agenda fechada e sessões encerradas. agendaId={}", agendaId));
            })
            .doOnError(err -> LOGGER.error("[adapter.closeAgenda] Erro ao fechar agenda. agendaId={}", agendaId, err))
            .map(agendaCycleMapper::toDomain);
    }
    
    @Override
    public Mono<Agenda> addVote(String sessionId, String userId, String cpf, String voteType) {
        String maskedCpf = cpf != null && cpf.length() >= 4 ? "***********".substring(0, Math.max(0, cpf.length() - 4)) + cpf.substring(cpf.length() - 4) : "***";
        LOGGER.info("[adapter.addVote] Registrando voto. sessionId={}, userId={}, cpfMasked={}, voteType={}", sessionId, userId, maskedCpf, voteType);
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
                        LOGGER.warn("[adapter.addVote] Tipo de voto inválido. sessionId={}, voteType={}", sessionId, voteType);
                        return Mono.error(new IllegalArgumentException("Tipo de voto inválido: " + voteType));
                    }
                }
                return agendaCycleRepository.save(agendaCycle)
                    .doOnSuccess(e -> LOGGER.info("[adapter.addVote] Voto persistido. sessionId={}, userId={}", sessionId, userId));
            })
            .doOnError(err -> LOGGER.warn("[adapter.addVote] Falha ao persistir voto. sessionId={}, userId={}", sessionId, userId, err))
            .map(agendaCycleMapper::toDomain);
    }
    
    private SessionEntity createNewSessionEntity(LocalDateTime startTime, Integer durationMinutes) {
        return SessionEntity.createNew(startTime, durationMinutes);
    }
}
