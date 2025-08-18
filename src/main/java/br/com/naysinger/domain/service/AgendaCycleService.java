package br.com.naysinger.domain.service;

import br.com.naysinger.common.enums.SessionStatus;
import br.com.naysinger.domain.model.AgendaCycle;
import br.com.naysinger.domain.model.Session;
import br.com.naysinger.domain.port.AgendaCyclePort;
import br.com.naysinger.common.exception.BusinessException;
import br.com.naysinger.common.exception.DuplicateCpfException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Service
public class AgendaCycleService {
    
    private final AgendaCyclePort agendaCyclePort;
    
    public AgendaCycleService(AgendaCyclePort agendaCyclePort) {
        this.agendaCyclePort = agendaCyclePort;
    }
    
    /**
     * Cria uma nova agenda
     */
    public Mono<AgendaCycle> createAgendaCycle(AgendaCycle agendaCycle) {
        return agendaCyclePort.save(agendaCycle);
    }
    
    /**
     * Busca uma agenda por ID
     */
    public Mono<AgendaCycle> findById(String id) {
        return agendaCyclePort.findById(id)
            .switchIfEmpty(Mono.error(new BusinessException("Agenda não encontrada com ID: " + id)));
    }
    
    /**
     * Busca uma agenda por agendaId
     */
    public Mono<AgendaCycle> findByAgendaId(String agendaId) {
        return agendaCyclePort.findByAgendaId(agendaId)
            .switchIfEmpty(Mono.error(new BusinessException("Agenda não encontrada com agendaId: " + agendaId)));
    }
    
    /**
     * Busca uma agenda por sessionId
     */
    public Mono<AgendaCycle> findBySessionId(String sessionId) {
        return agendaCyclePort.findBySessionId(sessionId)
            .switchIfEmpty(Mono.error(new BusinessException("Sessão não encontrada com sessionId: " + sessionId)));
    }
    
    /**
     * Busca todas as agendas
     */
    public Flux<AgendaCycle> findAll() {
        return agendaCyclePort.findAll();
    }
    
    /**
     * Busca agendas com sessões ativas
     */
    public Flux<AgendaCycle> findActiveSessions() {
        return agendaCyclePort.findAgendasWithActiveSession();
    }
    
    /**
     * Adiciona uma sessão a uma agenda existente
     */
    public Mono<AgendaCycle> addSession(String agendaId, LocalDateTime startTime, Integer durationMinutes) {
        return agendaCyclePort.findByAgendaId(agendaId)
            .switchIfEmpty(Mono.error(new BusinessException("Agenda não encontrada com ID: " + agendaId)))
            .flatMap(agendaCycle -> {
                // Validar se já existe uma sessão ativa (não expirada) para esta agenda
                if (agendaCycle.hasActiveSession()) {
                    return Mono.error(new BusinessException("Já existe uma sessão ativa (não expirada) para esta agenda. Aguarde a sessão atual expirar ou feche-a antes de criar uma nova."));
                }
                
                // Validar se a data de início é no futuro
                if (startTime.isBefore(LocalDateTime.now())) {
                    return Mono.error(new BusinessException("A data de início deve ser no futuro"));
                }
                
                // Validar duração mínima
                if (durationMinutes < 1) {
                    return Mono.error(new BusinessException("A duração deve ser de pelo menos 1 minuto"));
                }
                
                // Adicionar sessão
                return agendaCyclePort.addSession(agendaId, startTime, durationMinutes);
            });
    }
    
    /**
     * Fecha uma sessão
     */
    public Mono<AgendaCycle> closeSession(String sessionId) {
        return agendaCyclePort.closeSession(sessionId);
    }
    
    /**
     * Adiciona um voto a uma sessão
     */
    public Mono<AgendaCycle> addVote(String sessionId, String userId, String cpf, String voteType) {
        return agendaCyclePort.findBySessionId(sessionId)
            .switchIfEmpty(Mono.error(new BusinessException("Sessão não encontrada com sessionId: " + sessionId)))
            .flatMap(agendaCycle -> {
                // Obter a sessão específica
                Session targetSession = agendaCycle.getSessionById(sessionId);
                if (targetSession == null) {
                    return Mono.error(new BusinessException("Sessão não encontrada com sessionId: " + sessionId));
                }
                
                // Validar se a sessão está em andamento (já começou e não expirou)
                if (!targetSession.isInProgress()) {
                    if (!targetSession.hasStarted()) {
                        return Mono.error(new BusinessException("Sessão ainda não começou. Aguarde o horário de início."));
                    } else if (targetSession.getStatus() != SessionStatus.OPEN) {
                        return Mono.error(new BusinessException("Sessão não está aberta para votação"));
                    } else {
                        return Mono.error(new BusinessException("Sessão já expirou"));
                    }
                }
                
                // Validar se o CPF já votou nesta sessão
                if (targetSession.getVotes() != null) {
                    boolean cpfJaVotou = targetSession.getVotes().stream()
                        .anyMatch(vote -> vote.getCpf().equals(cpf));
                    
                    if (cpfJaVotou) {
                        return Mono.error(new DuplicateCpfException(cpf));
                    }
                }
                
                // Adicionar voto
                return agendaCyclePort.addVote(sessionId, userId, cpf, voteType);
            });
    }
}
