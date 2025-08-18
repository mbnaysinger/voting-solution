package br.com.naysinger.infrastructure.entity;

import br.com.naysinger.common.enums.AgendaStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "agenda_cycle")
public class AgendaCycleEntity {
    
    @Id
    private String id;
    
    @Field("agenda_id")
    @Indexed(unique = true)
    private String agendaId;
    
    @Field("title")
    private String title;
    
    @Field("description")
    private String description;
    
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("status")
    private AgendaStatus status;
    
    @Field("sessions")
    private List<SessionEntity> sessions;
    
    @Field("created_by")
    private String createdBy;
    
    // Construtor padrão
    public AgendaCycleEntity() {
        this.sessions = new ArrayList<>();
    }
    
    // Construtor com todos os campos
    public AgendaCycleEntity(String id, String agendaId, String title, String description, 
                           LocalDateTime createdAt, AgendaStatus status, List<SessionEntity> sessions, String createdBy) {
        this.id = id;
        this.agendaId = agendaId;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.status = status;
        this.sessions = sessions != null ? sessions : new ArrayList<>();
        this.createdBy = createdBy;
    }
    
    // Getters e Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getAgendaId() {
        return agendaId;
    }
    
    public void setAgendaId(String agendaId) {
        this.agendaId = agendaId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public AgendaStatus getStatus() {
        return status;
    }
    
    public void setStatus(AgendaStatus status) {
        this.status = status;
    }
    
    public List<SessionEntity> getSessions() {
        return sessions;
    }
    
    public void setSessions(List<SessionEntity> sessions) {
        this.sessions = sessions;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
