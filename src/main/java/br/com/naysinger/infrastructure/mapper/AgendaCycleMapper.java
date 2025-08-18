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
     * Converte AgendaCycleEntity para Agenda (domínio)
     */
    public Agenda toDomain(AgendaCycleEntity entity) {
        if (entity == null) {
            return null;
        }

        Agenda.Builder agendaBuilder = Agenda.builder()
                .id(entity.getId())
                .agendaId(entity.getAgendaId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy());

        // Converter sessões se existirem
        if (entity.getSessions() != null && !entity.getSessions().isEmpty()) {
            List<Session> sessions = entity.getSessions().stream()
                    .map(this::convertSessionEntityToSession)
                    .collect(Collectors.toList());
            agendaBuilder.sessions(sessions);
        }

        return agendaBuilder.build();
    }

    /**
     * Converte Agenda (domínio) para AgendaCycleEntity
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

        Session.Builder sessionBuilder = Session.builder()
                .sessionId(entity.getSessionId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .status(entity.getStatus());

        // Converter votos se existirem
        if (entity.getVotes() != null && !entity.getVotes().isEmpty()) {
            List<Vote> votes = entity.getVotes().stream()
                    .map(this::convertVoteEntityToVote)
                    .collect(Collectors.toList());
            sessionBuilder.votes(votes);
        }

        return sessionBuilder.build();
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
        if (session.getVotes() != null && !session.getVotes().isEmpty()) {
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

        return Vote.builder()
                .userId(entity.getUserId())
                .cpf(entity.getCpf())
                .vote(entity.getVote())
                .timestamp(entity.getTimestamp())
                .build();
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

    /**
     * Converte lista de AgendaCycleEntity para lista de Agenda (domínio)
     */
    public List<Agenda> toDomainList(List<AgendaCycleEntity> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Converte lista de Agenda (domínio) para lista de AgendaCycleEntity
     */
    public List<AgendaCycleEntity> toEntityList(List<Agenda> agendas) {
        if (agendas == null) {
            return null;
        }

        return agendas.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}