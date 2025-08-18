package br.com.naysinger.infrastructure.entity;

import br.com.naysinger.common.enums.AgendaStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "agenda_cycle")
public class AgendaCycleEntity {

    @Id
    private String id;

    @Field("agenda_id")
    @Indexed(unique = true)
    private String agendaId;

    private String title;

    private String description;

    @Field("created_at")
    private LocalDateTime createdAt;

    private AgendaStatus status;

    @Field("session")
    private SessionEntity session;

    @Field("created_by")
    private String createdBy;

    public AgendaCycleEntity() {}

    public AgendaCycleEntity(String agendaId, String title, String description, AgendaStatus status, SessionEntity session, String createdBy) {
        this.agendaId = agendaId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.session = session;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }

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

    public SessionEntity getSession() {
        return session;
    }

    public void setSession(SessionEntity session) {
        this.session = session;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "AgendaCycle{" +
                "id='" + id + '\'' +
                ", agendaId='" + agendaId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", status=" + status +
                ", session=" + session +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
