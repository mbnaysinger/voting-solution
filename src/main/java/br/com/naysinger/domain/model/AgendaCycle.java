package br.com.naysinger.domain.model;

import br.com.naysinger.common.enums.AgendaStatus;

import java.time.LocalDateTime;

public class AgendaCycle {
    
    private String id;
    private String agendaId;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private AgendaStatus status;
    private Session session;
    private String createdBy;
    
    // Construtor padrão
    public AgendaCycle() {}
    
    // Construtor com todos os campos
    public AgendaCycle(String id, String agendaId, String title, String description, 
                      LocalDateTime createdAt, AgendaStatus status, Session session, String createdBy) {
        this.id = id;
        this.agendaId = agendaId;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.status = status;
        this.session = session;
        this.createdBy = createdBy;
    }
    
    // Construtor para criar nova agenda
    public static AgendaCycle createNew(String agendaId, String title, String description, String createdBy) {
        return new AgendaCycle(
            null,
            agendaId,
            title,
            description,
            LocalDateTime.now(),
            AgendaStatus.PENDING,
            null,
            createdBy
        );
    }
    
    // Método para adicionar sessão
    public void addSession(LocalDateTime startTime, Integer durationMinutes) {
        if (this.session != null) {
            throw new IllegalStateException("Agenda já possui uma sessão");
        }
        
        this.session = Session.createNew(this.agendaId, startTime, durationMinutes);
    }
    
    // Método para obter sessão
    public Session getSession() {
        return session;
    }
    
    // Método para verificar se tem sessão
    public boolean hasSession() {
        return session != null;
    }
    
    // Método para verificar se tem sessão ativa
    public boolean hasActiveSession() {
        return session != null && session.isActive();
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
    
    public void setSession(Session session) {
        this.session = session;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
