package br.com.naysinger.api.mapper;

import br.com.naysinger.api.dto.AgendaRequestDTO;
import br.com.naysinger.api.dto.AgendaResponseDTO;
import br.com.naysinger.domain.model.AgendaCycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AgendaMapper {
    
    @Autowired
    private SessionMapper sessionMapper;
    
    /**
     * Converte AgendaCycleRequestDTO para AgendaCycle (domínio)
     */
    public AgendaCycle toDomain(AgendaRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        // Gerar agendaId automático
        String agendaId = "agenda_" + UUID.randomUUID().toString().substring(0, 8);
        
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
     * Converte AgendaCycle (domínio) para AgendaCycleResponseDTO
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
        
        // Converter sessão se existir usando o SessionMapper
        if (agendaCycle.getSession() != null) {
            dto.setSession(sessionMapper.toDTO(agendaCycle.getSession()));
        }
        
        return dto;
    }
}
