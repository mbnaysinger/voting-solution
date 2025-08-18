package br.com.naysinger.domain.service;

import br.com.naysinger.domain.model.Agenda;
import br.com.naysinger.domain.model.Session;
import br.com.naysinger.domain.port.AgendaPort;
import br.com.naysinger.common.exception.BusinessException;
import br.com.naysinger.common.exception.DuplicateCpfException;
import br.com.naysinger.common.enums.AgendaStatus;
import br.com.naysinger.common.enums.SessionStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Service
public class AgendaService {
    
    private final AgendaPort agendaPort;
    
    public AgendaService(AgendaPort agendaPort) {
        this.agendaPort = agendaPort;
    }
    
    /**
     * Cria uma nova agenda
     */
    public Mono<Agenda> createAgenda(Agenda agenda) {
        return agendaPort.save(agenda);
    }
    
    /**
     * Busca uma agenda por ID
     */
    public Mono<Agenda> findById(String id) {
        return agendaPort.findById(id)
            .switchIfEmpty(Mono.error(new BusinessException("Agenda não encontrada com ID: " + id)));
    }
    
    /**
     * Busca uma agenda por agendaId
     */
    public Mono<Agenda> findByAgendaId(String agendaId) {
        return agendaPort.findByAgendaId(agendaId)
            .switchIfEmpty(Mono.error(new BusinessException("Agenda não encontrada com agendaId: " + agendaId)));
    }
    
    /**
     * Busca uma agenda por sessionId
     */
    public Mono<Agenda> findBySessionId(String sessionId) {
        return agendaPort.findBySessionId(sessionId)
            .switchIfEmpty(Mono.error(new BusinessException("Sessão não encontrada com sessionId: " + sessionId)));
    }
    
    /**
     * Busca todas as agendas
     */
    public Flux<Agenda> findAll() {
        return agendaPort.findAll();
    }
    
    /**
     * Busca agendas com sessões ativas
     */
    public Flux<Agenda> findActiveSessions() {
        return agendaPort.findAgendasWithActiveSession();
    }
    
    /**
     * Adiciona uma sessão a uma agenda existente
     */
    public Mono<Agenda> addSession(String agendaId, LocalDateTime startTime, Integer durationMinutes) {
        return agendaPort.findByAgendaId(agendaId)
            .switchIfEmpty(Mono.error(new BusinessException("Agenda não encontrada com ID: " + agendaId)))
            .flatMap(agenda -> {
                // Validar se já existe uma sessão ativa (não expirada) para esta agenda
                if (agenda.hasActiveSession()) {
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
                return agendaPort.addSession(agendaId, startTime, durationMinutes);
            });
    }
    
    /**
     * Fecha uma sessão
     */
    public Mono<Agenda> closeSession(String sessionId) {
        return agendaPort.closeSession(sessionId);
    }
    
    /**
     * Fecha uma agenda e todas as suas sessões abertas
     */
    public Mono<Agenda> closeAgenda(String agendaId) {
        return agendaPort.findByAgendaId(agendaId)
            .switchIfEmpty(Mono.error(new BusinessException("Agenda não encontrada com agendaId: " + agendaId)))
            .flatMap(agenda -> {
                // Validar se a agenda já está fechada
                if (agenda.getStatus() == AgendaStatus.CLOSED) {
                    return Mono.error(new BusinessException("Agenda já está fechada"));
                }
                
                // Fechar a agenda e todas as sessões abertas
                return agendaPort.closeAgenda(agendaId);
            });
    }
    
    /**
     * Adiciona um voto a uma sessão
     */
    public Mono<Agenda> addVote(String sessionId, String userId, String cpf, String voteType) {
        return agendaPort.findBySessionId(sessionId)
            .switchIfEmpty(Mono.error(new BusinessException("Sessão não encontrada com sessionId: " + sessionId)))
            .flatMap(agenda -> {
                // Obter a sessão específica
                Session targetSession = agenda.getSessionById(sessionId);
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
                return agendaPort.addVote(sessionId, userId, cpf, voteType);
            });
    }
}
