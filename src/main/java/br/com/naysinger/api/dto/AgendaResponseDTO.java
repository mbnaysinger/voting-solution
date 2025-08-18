package br.com.naysinger.api.dto;

import br.com.naysinger.api.dto.session.SessionDTO;
import br.com.naysinger.common.enums.AgendaStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class AgendaResponseDTO {
    
    // Dados da Agenda
    private String id;
    private String agendaId;
    private String title;
    private String description;
    private AgendaStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    private String createdBy;
    
    // Dados das Sessões
    private List<SessionDTO> sessions;
    
    // Construtor padrão
    public AgendaResponseDTO() {}
    
    // Construtor com todos os campos
    public AgendaResponseDTO(String id, String agendaId, String title, String description, 
                            AgendaStatus status, LocalDateTime createdAt, String createdBy, 
                            List<SessionDTO> sessions) {
        this.id = id;
        this.agendaId = agendaId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.sessions = sessions;
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
    
    public List<SessionDTO> getSessions() {
        return sessions;
    }
    
    public void setSessions(List<SessionDTO> sessions) {
        this.sessions = sessions;
    }
}
