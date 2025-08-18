package br.com.naysinger.domain.model;

import br.com.naysinger.common.enums.AgendaStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AgendaCycle {
    
    private String id;
    private String agendaId;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private AgendaStatus status;
    private List<Session> sessions;
    private String createdBy;
    
    // Construtor padrão
    public AgendaCycle() {
        this.sessions = new ArrayList<>();
    }
    
    // Construtor com todos os campos
    public AgendaCycle(String id, String agendaId, String title, String description, 
                      LocalDateTime createdAt, AgendaStatus status, List<Session> sessions, String createdBy) {
        this.id = id;
        this.agendaId = agendaId;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.status = status;
        this.sessions = sessions != null ? sessions : new ArrayList<>();
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
            new ArrayList<>(),
            createdBy
        );
    }
    
    // Método para adicionar sessão
    public void addSession(LocalDateTime startTime, Integer durationMinutes) {
        Session newSession = Session.createNew(startTime, durationMinutes);
        this.sessions.add(newSession);
    }
    
    // Método para obter sessões
    public List<Session> getSessions() {
        return sessions;
    }
    
    // Método para verificar se tem sessões
    public boolean hasSession() {
        return sessions != null && !sessions.isEmpty();
    }
    
    // Método para verificar se tem sessão ativa
    public boolean hasActiveSession() {
        return sessions != null && sessions.stream().anyMatch(Session::isActive);
    }
    
    // Método para verificar se tem sessão em andamento (já começou e não expirou)
    public boolean hasSessionInProgress() {
        return sessions != null && sessions.stream().anyMatch(Session::isInProgress);
    }
    
    // Método para obter sessão ativa
    public Session getActiveSession() {
        if (sessions == null) return null;
        return sessions.stream()
            .filter(Session::isActive)
            .findFirst()
            .orElse(null);
    }
    
    // Método para obter sessão em andamento
    public Session getSessionInProgress() {
        if (sessions == null) return null;
        return sessions.stream()
            .filter(Session::isInProgress)
            .findFirst()
            .orElse(null);
    }
    
    // Método para obter sessão por ID
    public Session getSessionById(String sessionId) {
        if (sessions == null) return null;
        return sessions.stream()
            .filter(s -> s.getSessionId().equals(sessionId))
            .findFirst()
            .orElse(null);
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
    
    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
