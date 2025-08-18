package br.com.naysinger.api.mapper;

import br.com.naysinger.api.dto.AgendaRequestDTO;
import br.com.naysinger.api.dto.AgendaResponseDTO;
import br.com.naysinger.api.dto.session.SessionDTO;
import br.com.naysinger.domain.model.Agenda;
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
    public Agenda toDomain(AgendaRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        // Gerar agendaId automático
        String agendaId = "agenda_" + System.currentTimeMillis();
        
        Agenda agenda = Agenda.createNew(
            agendaId,
            dto.getTitle(),
            dto.getDescription(),
            dto.getCreatedBy()
        );
        
        // Se foram fornecidos dados da sessão, criar a sessão também
        if (dto.getSessionStartTime() != null && dto.getSessionDurationMinutes() != null) {
            agenda.addSession(dto.getSessionStartTime(), dto.getSessionDurationMinutes());
        }
        
        return agenda;
    }
    
    /**
     * Converte AgendaCycle (domínio) para AgendaResponseDTO
     */
    public AgendaResponseDTO toResponse(Agenda agenda) {
        if (agenda == null) {
            return null;
        }
        
        AgendaResponseDTO dto = new AgendaResponseDTO();
        dto.setId(agenda.getId());
        dto.setAgendaId(agenda.getAgendaId());
        dto.setTitle(agenda.getTitle());
        dto.setDescription(agenda.getDescription());
        dto.setStatus(agenda.getStatus());
        dto.setCreatedAt(agenda.getCreatedAt());
        dto.setCreatedBy(agenda.getCreatedBy());
        
        // Converter sessões se existirem usando o SessionMapper
        if (agenda.getSessions() != null && !agenda.getSessions().isEmpty()) {
            List<SessionDTO> sessions = agenda.getSessions().stream()
                .map(sessionMapper::toDTO)
                .collect(Collectors.toList());
            dto.setSessions(sessions);
        }
        
        return dto;
    }
}
