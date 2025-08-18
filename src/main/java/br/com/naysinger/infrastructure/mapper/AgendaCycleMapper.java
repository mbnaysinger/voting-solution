package br.com.naysinger.infrastructure.mapper;

import br.com.naysinger.domain.model.AgendaCycle;
import br.com.naysinger.infrastructure.entity.AgendaCycleEntity;
import org.springframework.stereotype.Component;

@Component
public class AgendaCycleMapper {
    
    /**
     * Converte AgendaCycleEntity para AgendaCycle (domínio)
     */
    public AgendaCycle toDomain(AgendaCycleEntity entity) {
        if (entity == null) {
            return null;
        }
        
        AgendaCycle agendaCycle = new AgendaCycle();
        agendaCycle.setId(entity.getId());
        agendaCycle.setAgendaId(entity.getAgendaId());
        agendaCycle.setTitle(entity.getTitle());
        agendaCycle.setDescription(entity.getDescription());
        agendaCycle.setStatus(entity.getStatus());
        agendaCycle.setCreatedAt(entity.getCreatedAt());
        agendaCycle.setCreatedBy(entity.getCreatedBy());
        
        // Converter sessão se existir
        if (entity.getSession() != null) {
            AgendaCycle.Session session = convertSessionEntityToSession(entity.getSession());
            agendaCycle.setSession(session);
        }
        
        return agendaCycle;
    }
    
    /**
     * Converte AgendaCycle (domínio) para AgendaCycleEntity
     */
    public AgendaCycleEntity toEntity(AgendaCycle agendaCycle) {
        if (agendaCycle == null) {
            return null;
        }
        
        AgendaCycleEntity entity = new AgendaCycleEntity();
        entity.setId(agendaCycle.getId());
        entity.setAgendaId("agenda_" + System.currentTimeMillis());
        entity.setTitle(agendaCycle.getTitle());
        entity.setDescription(agendaCycle.getDescription());
        entity.setStatus(agendaCycle.getStatus());
        entity.setCreatedAt(agendaCycle.getCreatedAt());
        entity.setCreatedBy(agendaCycle.getCreatedBy());
        
        // Converter sessão se existir
        if (agendaCycle.getSession() != null) {
            br.com.naysinger.infrastructure.entity.SessionEntity sessionEntity = convertSessionToSessionEntity(agendaCycle.getSession());
            entity.setSession(sessionEntity);
        }
        
        return entity;
    }
    
    /**
     * Converte SessionEntity para Session (domínio)
     */
    private AgendaCycle.Session convertSessionEntityToSession(br.com.naysinger.infrastructure.entity.SessionEntity entity) {
        if (entity == null) {
            return null;
        }
        
        AgendaCycle.Session session = new AgendaCycle.Session();
        session.setSessionId(entity.getSessionId());
        session.setStartTime(entity.getStartTime());
        session.setEndTime(entity.getEndTime());
        session.setStatus(entity.getStatus());
        
        // Converter votos se existirem
        if (entity.getVotes() != null) {
            java.util.List<AgendaCycle.Session.Vote> votes = entity.getVotes().stream()
                .map(this::convertVoteEntityToVote)
                .collect(java.util.stream.Collectors.toList());
            session.setVotes(votes);
        }
        
        return session;
    }
    
    /**
     * Converte Session (domínio) para SessionEntity
     */
    private br.com.naysinger.infrastructure.entity.SessionEntity convertSessionToSessionEntity(AgendaCycle.Session session) {
        if (session == null) {
            return null;
        }
        
        br.com.naysinger.infrastructure.entity.SessionEntity entity = new br.com.naysinger.infrastructure.entity.SessionEntity();
        entity.setSessionId("session_" + System.currentTimeMillis());
        entity.setStartTime(session.getStartTime());
        entity.setEndTime(session.getEndTime());
        entity.setStatus(session.getStatus());
        
        // Converter votos se existirem
        if (session.getVotes() != null) {
            java.util.List<br.com.naysinger.infrastructure.entity.VoteEntity> votes = session.getVotes().stream()
                .map(this::convertVoteToVoteEntity)
                .collect(java.util.stream.Collectors.toList());
            entity.setVotes(votes);
        }
        
        return entity;
    }
    
    /**
     * Converte VoteEntity para Vote (domínio)
     */
    private AgendaCycle.Session.Vote convertVoteEntityToVote(br.com.naysinger.infrastructure.entity.VoteEntity entity) {
        if (entity == null) {
            return null;
        }
        
        AgendaCycle.Session.Vote vote = new AgendaCycle.Session.Vote();
        vote.setUserId(entity.getUserId());
        vote.setCpf(entity.getCpf());
        vote.setVote(entity.getVote());
        vote.setTimestamp(entity.getTimestamp());
        
        return vote;
    }
    
    /**
     * Converte Vote (domínio) para VoteEntity
     */
    private br.com.naysinger.infrastructure.entity.VoteEntity convertVoteToVoteEntity(AgendaCycle.Session.Vote vote) {
        if (vote == null) {
            return null;
        }
        
        br.com.naysinger.infrastructure.entity.VoteEntity entity = new br.com.naysinger.infrastructure.entity.VoteEntity();
        entity.setUserId(vote.getUserId());
        entity.setCpf(vote.getCpf());
        entity.setVote(vote.getVote());
        entity.setTimestamp(vote.getTimestamp());
        
        return entity;
    }
}
