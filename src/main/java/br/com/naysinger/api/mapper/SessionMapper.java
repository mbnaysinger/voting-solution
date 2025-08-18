package br.com.naysinger.api.mapper;

import br.com.naysinger.api.dto.session.SessionDTO;
import br.com.naysinger.api.dto.vote.VoteDTO;
import br.com.naysinger.domain.model.Session;
import br.com.naysinger.domain.model.Vote;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SessionMapper {
    
    /**
     * Converte Session (domínio) para SessionDTO
     */
    public SessionDTO toDTO(Session session) {
        if (session == null) {
            return null;
        }
        
        SessionDTO dto = new SessionDTO();
        dto.setSessionId(session.getSessionId());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setStatus(session.getStatus());
        dto.setTotalVotes(session.getTotalVotes());
        
        // Converter votos se existirem
        if (session.getVotes() != null) {
            List<VoteDTO> votes = session.getVotes().stream()
                .map(this::voteToDTO)
                .collect(Collectors.toList());
            dto.setVotes(votes);
        }
        
        return dto;
    }
    
    /**
     * Converte Vote (domínio) para VoteDTO
     */
    private VoteDTO voteToDTO(Vote vote) {
        if (vote == null) {
            return null;
        }
        
        VoteDTO dto = new VoteDTO();
        dto.setUserId(vote.getUserId());
        dto.setCpf(vote.getCpf());
        dto.setVote(vote.getVote().name());
        dto.setTimestamp(vote.getTimestamp());
        
        return dto;
    }
}
