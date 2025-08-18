package br.com.naysinger.api.mapper;

import br.com.naysinger.api.dto.AgendaCycleRequestDTO;
import br.com.naysinger.api.dto.AgendaCycleResponseDTO;
import br.com.naysinger.domain.model.AgendaCycle;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AgendaMapper {
    
    /**
     * Converte AgendaCycleRequestDTO para AgendaCycle (domínio)
     */
    public AgendaCycle toDomain(AgendaCycleRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        AgendaCycle agendaCycle = AgendaCycle.createNew(
                null,
            dto.getTitle(),
            dto.getDescription(),
            dto.getCreatedBy()
        );
        
        // Se foram fornecidos dados da sessão, criar a sessão também
        if (dto.getSessionStartTime() != null && dto.getSessionDurationMinutes() != null) {
            agendaCycle.addSession(dto.getSessionStartTime(), dto.getSessionDurationMinutes());
        }
        
        return agendaCycle;
    }
    
    /**
     * Converte AgendaCycle (domínio) para AgendaCycleResponseDTO
     */
    public AgendaCycleResponseDTO toResponse(AgendaCycle agendaCycle) {
        if (agendaCycle == null) {
            return null;
        }
        
        AgendaCycleResponseDTO dto = new AgendaCycleResponseDTO();
        dto.setId(agendaCycle.getId());
        dto.setAgendaId(agendaCycle.getAgendaId());
        dto.setTitle(agendaCycle.getTitle());
        dto.setDescription(agendaCycle.getDescription());
        dto.setStatus(agendaCycle.getStatus());
        dto.setCreatedAt(agendaCycle.getCreatedAt());
        dto.setCreatedBy(agendaCycle.getCreatedBy());
        
        // Converter sessão se existir
        if (agendaCycle.getSession() != null) {
            AgendaCycleResponseDTO.SessionInfoDTO sessionDto = convertSessionToSessionInfo(agendaCycle.getSession());
            dto.setSession(sessionDto);
        }
        
        return dto;
    }
    
    /**
     * Converte Session (domínio) para SessionInfoDTO
     */
    private AgendaCycleResponseDTO.SessionInfoDTO convertSessionToSessionInfo(AgendaCycle.Session session) {
        if (session == null) {
            return null;
        }
        
        AgendaCycleResponseDTO.SessionInfoDTO sessionDto = new AgendaCycleResponseDTO.SessionInfoDTO();
        sessionDto.setSessionId(session.getSessionId());
        sessionDto.setStartTime(session.getStartTime());
        sessionDto.setEndTime(session.getEndTime());
        sessionDto.setStatus(session.getStatus());
        sessionDto.setTotalVotes(session.getTotalVotes());
        
        // Converter votos se existirem
        if (session.getVotes() != null) {
            List<AgendaCycleResponseDTO.SessionInfoDTO.VoteInfoDTO> votes = session.getVotes().stream()
                .map(this::convertVoteToVoteInfo)
                .collect(Collectors.toList());
            sessionDto.setVotes(votes);
        }
        
        return sessionDto;
    }
    
    /**
     * Converte Vote (domínio) para VoteInfoDTO
     */
    private AgendaCycleResponseDTO.SessionInfoDTO.VoteInfoDTO convertVoteToVoteInfo(AgendaCycle.Session.Vote vote) {
        if (vote == null) {
            return null;
        }
        
        AgendaCycleResponseDTO.SessionInfoDTO.VoteInfoDTO voteDto = new AgendaCycleResponseDTO.SessionInfoDTO.VoteInfoDTO();
        voteDto.setUserId(vote.getUserId());
        voteDto.setCpf(vote.getCpf());
        voteDto.setVote(vote.getVote().name());
        voteDto.setTimestamp(vote.getTimestamp());
        
        return voteDto;
    }
}
