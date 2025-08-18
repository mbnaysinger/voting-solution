package br.com.naysinger.infrastructure.mapper;

import br.com.naysinger.domain.model.Agenda;
import br.com.naysinger.domain.model.Session;
import br.com.naysinger.domain.model.Vote;
import br.com.naysinger.infrastructure.entity.AgendaCycleEntity;
import br.com.naysinger.infrastructure.entity.SessionEntity;
import br.com.naysinger.infrastructure.entity.VoteEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AgendaCycleMapper {
    
    /**
     * Converte AgendaCycleEntity para AgendaCycle (domínio)
     */
    public Agenda toDomain(AgendaCycleEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Agenda agenda = new Agenda();
        agenda.setId(entity.getId());
        agenda.setAgendaId(entity.getAgendaId());
        agenda.setTitle(entity.getTitle());
        agenda.setDescription(entity.getDescription());
        agenda.setStatus(entity.getStatus());
        agenda.setCreatedAt(entity.getCreatedAt());
        agenda.setCreatedBy(entity.getCreatedBy());
        
        // Converter sessões se existirem
        if (entity.getSessions() != null && !entity.getSessions().isEmpty()) {
            List<Session> sessions = entity.getSessions().stream()
                .map(this::convertSessionEntityToSession)
                .collect(Collectors.toList());
            agenda.setSessions(sessions);
        }
        
        return agenda;
    }
    
    /**
     * Converte AgendaCycle (domínio) para AgendaCycleEntity
     */
    public AgendaCycleEntity toEntity(Agenda agenda) {
        if (agenda == null) {
            return null;
        }
        
        AgendaCycleEntity entity = new AgendaCycleEntity();
        entity.setId(agenda.getId());
        entity.setAgendaId(agenda.getAgendaId());
        entity.setTitle(agenda.getTitle());
        entity.setDescription(agenda.getDescription());
        entity.setStatus(agenda.getStatus());
        entity.setCreatedAt(agenda.getCreatedAt());
        entity.setCreatedBy(agenda.getCreatedBy());
        
        // Converter sessões se existirem
        if (agenda.getSessions() != null && !agenda.getSessions().isEmpty()) {
            List<SessionEntity> sessions = agenda.getSessions().stream()
                .map(this::convertSessionToSessionEntity)
                .collect(Collectors.toList());
            entity.setSessions(sessions);
        }
        
        return entity;
    }
    
    /**
     * Converte SessionEntity para Session (domínio)
     */
    private Session convertSessionEntityToSession(SessionEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Session session = new Session();
        session.setSessionId(entity.getSessionId());
        session.setStartTime(entity.getStartTime());
        session.setEndTime(entity.getEndTime());
        session.setStatus(entity.getStatus());
        
        // Converter votos se existirem
        if (entity.getVotes() != null) {
            List<Vote> votes = entity.getVotes().stream()
                .map(this::convertVoteEntityToVote)
                .collect(Collectors.toList());
            session.setVotes(votes);
        }
        
        return session;
    }
    
    /**
     * Converte Session (domínio) para SessionEntity
     */
    private SessionEntity convertSessionToSessionEntity(Session session) {
        if (session == null) {
            return null;
        }
        
        SessionEntity entity = new SessionEntity();
        entity.setSessionId(session.getSessionId());
        entity.setStartTime(session.getStartTime());
        entity.setEndTime(session.getEndTime());
        entity.setStatus(session.getStatus());
        
        // Converter votos se existirem
        if (session.getVotes() != null) {
            List<VoteEntity> votes = session.getVotes().stream()
                .map(this::convertVoteToVoteEntity)
                .collect(Collectors.toList());
            entity.setVotes(votes);
        }
        
        return entity;
    }
    
    /**
     * Converte VoteEntity para Vote (domínio)
     */
    private Vote convertVoteEntityToVote(VoteEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Vote vote = new Vote();
        vote.setUserId(entity.getUserId());
        vote.setCpf(entity.getCpf());
        vote.setVote(entity.getVote());
        vote.setTimestamp(entity.getTimestamp());
        
        return vote;
    }
    
    /**
     * Converte Vote (domínio) para VoteEntity
     */
    private VoteEntity convertVoteToVoteEntity(Vote vote) {
        if (vote == null) {
            return null;
        }
        
        VoteEntity entity = new VoteEntity();
        entity.setUserId(vote.getUserId());
        entity.setCpf(vote.getCpf());
        entity.setVote(vote.getVote());
        entity.setTimestamp(vote.getTimestamp());
        
        return entity;
    }
}
