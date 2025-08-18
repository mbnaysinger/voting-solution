package br.com.naysinger.domain.model;

import br.com.naysinger.common.enums.AgendaStatus;
import java.time.LocalDateTime;

public class Agenda {
    
    private String id;
    private String agendaId;
    private String title;
    private String description;
    private AgendaStatus status;
    private LocalDateTime createdAt;
    private String createdBy;
    
    // Construtor padrão
    public Agenda() {}
    
    // Construtor com todos os campos
    public Agenda(String id, String agendaId, String title, String description, 
                  AgendaStatus status, LocalDateTime createdAt, String createdBy) {
        this.id = id;
        this.agendaId = agendaId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }
    
    // Builder estático
    public static AgendaBuilder builder() {
        return new AgendaBuilder();
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getAgendaId() { return agendaId; }
    public void setAgendaId(String agendaId) { this.agendaId = agendaId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public AgendaStatus getStatus() { return status; }
    public void setStatus(AgendaStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    // Classe Builder interna
    public static class AgendaBuilder {
        private String id;
        private String agendaId;
        private String title;
        private String description;
        private AgendaStatus status;
        private LocalDateTime createdAt;
        private String createdBy;
        
        public AgendaBuilder id(String id) {
            this.id = id;
            return this;
        }
        
        public AgendaBuilder agendaId(String agendaId) {
            this.agendaId = agendaId;
            return this;
        }
        
        public AgendaBuilder title(String title) {
            this.title = title;
            return this;
        }
        
        public AgendaBuilder description(String description) {
            this.description = description;
            return this;
        }
        
        public AgendaBuilder status(AgendaStatus status) {
            this.status = status;
            return this;
        }
        
        public AgendaBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public AgendaBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }
        
        public Agenda build() {
            return new Agenda(id, agendaId, title, description, status, createdAt, createdBy);
        }
    }
}
