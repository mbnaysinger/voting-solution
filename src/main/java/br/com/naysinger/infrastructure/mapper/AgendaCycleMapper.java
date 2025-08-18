package br.com.naysinger.infrastructure.mapper;

import br.com.naysinger.domain.model.Agenda;
import br.com.naysinger.infrastructure.entity.AgendaCycleEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AgendaCycleMapper {
    
    /**
     * Converte Agenda (Model de domínio) para AgendaCycleEntity
     */
    public AgendaCycleEntity toEntity(Agenda agenda) {
        AgendaCycleEntity entity = new AgendaCycleEntity();
        entity.setAgendaId(agenda.getAgendaId() != null ? agenda.getAgendaId() : "agenda_" + UUID.randomUUID().toString().substring(0, 8));
        entity.setTitle(agenda.getTitle());
        entity.setDescription(agenda.getDescription());
        entity.setStatus(agenda.getStatus());
        entity.setCreatedAt(agenda.getCreatedAt());
        entity.setCreatedBy(agenda.getCreatedBy());
        entity.setSession(null); // Nova pauta não tem sessão ainda
        
        return entity;
    }
    
    /**
     * Converte AgendaCycleEntity para Agenda (Model de domínio)
     */
    public Agenda toDomainModel(AgendaCycleEntity entity) {
        return Agenda.builder()
                .id(entity.getId())
                .agendaId(entity.getAgendaId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}
