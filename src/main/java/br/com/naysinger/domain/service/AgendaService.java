package br.com.naysinger.domain.service;

import br.com.naysinger.domain.model.Agenda;
import br.com.naysinger.domain.model.Session;
import br.com.naysinger.domain.port.AgendaPort;
import br.com.naysinger.domain.port.CpfValidationPort;
import br.com.naysinger.common.exception.BusinessException;
import br.com.naysinger.common.exception.DuplicateCpfException;
import br.com.naysinger.common.enums.AgendaStatus;
import br.com.naysinger.common.enums.SessionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Service
public class AgendaService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AgendaService.class);
    
    private final AgendaPort agendaPort;
    private final CpfValidationPort cpfValidationPort;
    
    public AgendaService(AgendaPort agendaPort, CpfValidationPort cpfValidationPort) {
        this.agendaPort = agendaPort;
        this.cpfValidationPort = cpfValidationPort;
    }
    
    /**
     * Cria uma nova agenda
     */
    public Mono<Agenda> createAgenda(Agenda agenda) {
        LOGGER.info("[createAgenda] Criando agenda. title={}, createdBy={}", agenda.getTitle(), agenda.getCreatedBy());
        return agendaPort.save(agenda)
            .doOnSuccess(a -> LOGGER.info("[createAgenda] Agenda criada. agendaId={}", a.getAgendaId()))
            .doOnError(e -> LOGGER.error("[createAgenda] Falha ao criar agenda. title={}", agenda.getTitle(), e));
    }
    
    /**
     * Busca uma agenda por ID
     */
    public Mono<Agenda> findById(String id) {
        LOGGER.debug("[findById] Buscando agenda. id={}", id);
        return agendaPort.findById(id)
            .switchIfEmpty(Mono.error(new BusinessException("Agenda não encontrada com ID: " + id)))
            .doOnError(e -> LOGGER.error("[findById] Erro ao buscar agenda. id={}", id, e));
    }
    
    /**
     * Busca uma agenda por agendaId
     */
    public Mono<Agenda> findByAgendaId(String agendaId) {
        LOGGER.debug("[findByAgendaId] Buscando agenda. agendaId={}", agendaId);
        return agendaPort.findByAgendaId(agendaId)
            .switchIfEmpty(Mono.error(new BusinessException("Agenda não encontrada com agendaId: " + agendaId)))
            .doOnError(e -> LOGGER.error("[findByAgendaId] Erro ao buscar agenda. agendaId={}", agendaId, e));
    }
    
    /**
     * Busca uma agenda por sessionId
     */
    public Mono<Agenda> findBySessionId(String sessionId) {
        LOGGER.debug("[findBySessionId] Buscando por sessionId. sessionId={}", sessionId);
        return agendaPort.findBySessionId(sessionId)
            .switchIfEmpty(Mono.error(new BusinessException("Sessão não encontrada com sessionId: " + sessionId)))
            .doOnError(e -> LOGGER.error("[findBySessionId] Erro ao buscar por sessionId. sessionId={}", sessionId, e));
    }
    
    /**
     * Busca todas as agendas
     */
    public Flux<Agenda> findAll() {
        LOGGER.debug("[findAll] Listando agendas");
        return agendaPort.findAll()
            .doOnError(e -> LOGGER.error("[findAll] Erro ao listar agendas", e));
    }
    
    /**
     * Busca agendas com sessões ativas
     */
    public Flux<Agenda> findActiveSessions() {
        LOGGER.debug("[findActiveSessions] Listando agendas com sessões ativas");
        return agendaPort.findAgendasWithActiveSession()
            .doOnError(e -> LOGGER.error("[findActiveSessions] Erro ao listar agendas com sessões ativas", e));
    }
    
    /**
     * Adiciona uma sessão a uma agenda existente
     */
    public Mono<Agenda> addSession(String agendaId, LocalDateTime startTime, Integer durationMinutes) {
        LOGGER.info("[addSession] Solicitando criação de sessão. agendaId={}, startTime={}, durationMinutes={}", agendaId, startTime, durationMinutes);
        return agendaPort.findByAgendaId(agendaId)
            .switchIfEmpty(Mono.error(new BusinessException("Agenda não encontrada com ID: " + agendaId)))
            .flatMap(agenda -> {
                if (agenda.hasActiveSession()) {
                    LOGGER.warn("[addSession] Já existe sessão ativa. agendaId={}", agendaId);
                    return Mono.error(new BusinessException("Já existe uma sessão ativa (não expirada) para esta agenda. Aguarde a sessão atual expirar ou feche-a antes de criar uma nova."));
                }
                if (startTime.isBefore(LocalDateTime.now())) {
                    LOGGER.warn("[addSession] Data de início no passado. agendaId={}, startTime={}", agendaId, startTime);
                    return Mono.error(new BusinessException("A data de início deve ser no futuro"));
                }
                if (durationMinutes < 1) {
                    LOGGER.warn("[addSession] Duração inválida. agendaId={}, durationMinutes={}", agendaId, durationMinutes);
                    return Mono.error(new BusinessException("A duração deve ser de pelo menos 1 minuto"));
                }
                return agendaPort.addSession(agendaId, startTime, durationMinutes)
                    .doOnSuccess(a -> LOGGER.info("[addSession] Sessão criada. agendaId={}, sessionsCount={}", agendaId, a.getSessions() != null ? a.getSessions().size() : 0));
            })
            .doOnError(e -> LOGGER.error("[addSession] Erro ao criar sessão. agendaId={}", agendaId, e));
    }
    
    /**
     * Fecha uma sessão
     */
    public Mono<Agenda> closeSession(String sessionId) {
        LOGGER.info("[closeSession] Solicitando fechamento de sessão. sessionId={}", sessionId);
        return agendaPort.closeSession(sessionId)
            .doOnSuccess(a -> LOGGER.info("[closeSession] Sessão fechada. sessionId={}", sessionId))
            .doOnError(e -> LOGGER.error("[closeSession] Erro ao fechar sessão. sessionId={}", sessionId, e));
    }
    
    /**
     * Fecha uma agenda e todas as suas sessões abertas
     */
    public Mono<Agenda> closeAgenda(String agendaId) {
        LOGGER.info("[closeAgenda] Solicitando fechamento de agenda. agendaId={}", agendaId);
        return agendaPort.findByAgendaId(agendaId)
            .switchIfEmpty(Mono.error(new BusinessException("Agenda não encontrada com agendaId: " + agendaId)))
            .flatMap(agenda -> {
                if (agenda.getStatus() == AgendaStatus.CLOSED) {
                    LOGGER.warn("[closeAgenda] Agenda já fechada. agendaId={}", agendaId);
                    return Mono.error(new BusinessException("Agenda já está fechada"));
                }
                return agendaPort.closeAgenda(agendaId)
                    .doOnSuccess(a -> LOGGER.info("[closeAgenda] Agenda fechada com sucesso. agendaId={}", agendaId));
            })
            .doOnError(e -> LOGGER.error("[closeAgenda] Erro ao fechar agenda. agendaId={}", agendaId, e));
    }
    
    /**
     * Adiciona um voto a uma sessão
     */
    public Mono<Agenda> addVote(String sessionId, String userId, String cpf, String voteType) {
        String maskedCpf = cpf != null && cpf.length() >= 4 ? "***********".substring(0, Math.max(0, cpf.length() - 4)) + cpf.substring(cpf.length() - 4) : "***";
        LOGGER.info("[addVote] Registrando voto. sessionId={}, userId={}, cpfMasked={}, voteType={}", sessionId, userId, maskedCpf, voteType);
        return cpfValidationPort.check(cpf)
            .then(agendaPort.findBySessionId(sessionId))
            .switchIfEmpty(Mono.error(new BusinessException("Sessão não encontrada com sessionId: " + sessionId)))
            .flatMap(agenda -> {
                Session targetSession = agenda.getSessionById(sessionId);
                if (targetSession == null) {
                    LOGGER.warn("[addVote] Sessão não encontrada no agregado. sessionId={}", sessionId);
                    return Mono.error(new BusinessException("Sessão não encontrada com sessionId: " + sessionId));
                }
                if (!targetSession.isInProgress()) {
                    if (!targetSession.hasStarted()) {
                        LOGGER.warn("[addVote] Sessão ainda não começou. sessionId={}", sessionId);
                        return Mono.error(new BusinessException("Sessão ainda não começou. Aguarde o horário de início."));
                    } else if (targetSession.getStatus() != SessionStatus.OPEN) {
                        LOGGER.warn("[addVote] Sessão não está aberta. sessionId={}", sessionId);
                        return Mono.error(new BusinessException("Sessão não está aberta para votação"));
                    } else {
                        LOGGER.warn("[addVote] Sessão expirada. sessionId={}", sessionId);
                        return Mono.error(new BusinessException("Sessão já expirou"));
                    }
                }
                if (targetSession.getVotes() != null && targetSession.getVotes().stream().anyMatch(v -> v.getCpf().equals(cpf))) {
                    LOGGER.warn("[addVote] CPF já votou nesta sessão. sessionId={}, userId={}", sessionId, userId);
                    return Mono.error(new DuplicateCpfException(cpf));
                }
                return agendaPort.addVote(sessionId, userId, cpf, voteType)
                    .doOnSuccess(a -> LOGGER.info("[addVote] Voto computado. sessionId={}, userId={}", sessionId, userId));
            })
            .doOnError(e -> LOGGER.warn("[addVote] Falha ao registrar voto. sessionId={}, userId={}", sessionId, userId, e));
    }
}
