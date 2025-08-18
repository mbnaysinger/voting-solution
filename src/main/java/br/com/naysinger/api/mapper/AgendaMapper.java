package br.com.naysinger.api.mapper;

import br.com.naysinger.api.dto.AgendaResponse;
import br.com.naysinger.api.dto.CreateAgendaRequest;
import br.com.naysinger.domain.model.Agenda;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AgendaMapper {
    
    /**
     * Converte CreateAgendaRequest para Agenda (Model de domínio)
     */
    public Agenda toModel(CreateAgendaRequest request) {
        return Agenda.builder()
                .agendaId("agenda_" + UUID.randomUUID().toString().substring(0, 8))
                .title(request.getTitle())
                .description(request.getDescription())
                .status(br.com.naysinger.common.enums.AgendaStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .createdBy(request.getCreatedBy() != null ? request.getCreatedBy() : "system")
                .build();
    }
    
    /**
     * Converte Agenda (Model de domínio) para AgendaResponse
     */
    public AgendaResponse toDto(Agenda agenda) {
        return new AgendaResponse(
            agenda.getId(),
            agenda.getAgendaId(),
            agenda.getTitle(),
            agenda.getDescription(),
            agenda.getStatus(),
            agenda.getCreatedAt(),
            agenda.getCreatedBy()
        );
    }
}
