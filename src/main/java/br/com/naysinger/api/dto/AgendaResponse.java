package br.com.naysinger.api.dto;

import br.com.naysinger.common.enums.AgendaStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class AgendaResponse {
    
    private String id;
    private String agendaId;
    private String title;
    private String description;
    private AgendaStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    private String createdBy;
    
    // Construtor padrão
    public AgendaResponse() {}
    
    // Construtor com todos os campos
    public AgendaResponse(String id, String agendaId, String title, String description, 
                         AgendaStatus status, LocalDateTime createdAt, String createdBy) {
        this.id = id;
        this.agendaId = agendaId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
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
    
    public AgendaStatus getStatus() {
        return status;
    }
    
    public void setStatus(AgendaStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
