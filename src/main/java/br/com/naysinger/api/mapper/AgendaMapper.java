package br.com.naysinger.api.mapper;

import br.com.naysinger.api.dto.AgendaRequestDTO;
import br.com.naysinger.api.dto.AgendaResponseDTO;
import br.com.naysinger.api.dto.session.SessionDTO;
import br.com.naysinger.domain.model.AgendaCycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AgendaMapper {
    
    @Autowired
    private SessionMapper sessionMapper;
    
    /**
     * Converte AgendaRequestDTO para AgendaCycle (domínio)
     */
    public AgendaCycle toDomain(AgendaRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        // Gerar agendaId automático
        String agendaId = "agenda_" + System.currentTimeMillis();
        
        AgendaCycle agendaCycle = AgendaCycle.createNew(
            agendaId,
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
     * Converte AgendaCycle (domínio) para AgendaResponseDTO
     */
    public AgendaResponseDTO toResponse(AgendaCycle agendaCycle) {
        if (agendaCycle == null) {
            return null;
        }
        
        AgendaResponseDTO dto = new AgendaResponseDTO();
        dto.setId(agendaCycle.getId());
        dto.setAgendaId(agendaCycle.getAgendaId());
        dto.setTitle(agendaCycle.getTitle());
        dto.setDescription(agendaCycle.getDescription());
        dto.setStatus(agendaCycle.getStatus());
        dto.setCreatedAt(agendaCycle.getCreatedAt());
        dto.setCreatedBy(agendaCycle.getCreatedBy());
        
        // Converter sessões se existirem usando o SessionMapper
        if (agendaCycle.getSessions() != null && !agendaCycle.getSessions().isEmpty()) {
            List<SessionDTO> sessions = agendaCycle.getSessions().stream()
                .map(sessionMapper::toDTO)
                .collect(Collectors.toList());
            dto.setSessions(sessions);
        }
        
        return dto;
    }
}
