package br.com.naysinger.api.dto;

import br.com.naysinger.common.enums.AgendaStatus;
import br.com.naysinger.common.enums.SessionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class AgendaCycleResponseDTO {
    
    // Dados da Agenda
    private String id;
    private String agendaId;
    private String title;
    private String description;
    private AgendaStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    private String createdBy;
    
    // Dados da Sessão
    private SessionInfoDTO session;
    
    // Construtor padrão
    public AgendaCycleResponseDTO() {}
    
    // Construtor com todos os campos
    public AgendaCycleResponseDTO(String id, String agendaId, String title, String description, 
                                 AgendaStatus status, LocalDateTime createdAt, String createdBy, 
                                 SessionInfoDTO session) {
        this.id = id;
        this.agendaId = agendaId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.session = session;
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
    
    public SessionInfoDTO getSession() {
        return session;
    }
    
    public void setSession(SessionInfoDTO session) {
        this.session = session;
    }
    
    // Classe interna para informações da sessão
    public static class SessionInfoDTO {
        private String sessionId;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime startTime;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime endTime;
        
        private SessionStatus status;
        private Integer totalVotes;
        private List<VoteInfoDTO> votes;
        
        // Construtor padrão
        public SessionInfoDTO() {}
        
        // Construtor com todos os campos
        public SessionInfoDTO(String sessionId, LocalDateTime startTime, LocalDateTime endTime, 
                             SessionStatus status, Integer totalVotes, List<VoteInfoDTO> votes) {
            this.sessionId = sessionId;
            this.startTime = startTime;
            this.endTime = endTime;
            this.status = status;
            this.totalVotes = totalVotes;
            this.votes = votes;
        }
        
        // Getters e Setters
        public String getSessionId() {
            return sessionId;
        }
        
        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
        
        public LocalDateTime getStartTime() {
            return startTime;
        }
        
        public void setStartTime(LocalDateTime startTime) {
            this.startTime = startTime;
        }
        
        public LocalDateTime getEndTime() {
            return endTime;
        }
        
        public void setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
        }
        
        public SessionStatus getStatus() {
            return status;
        }
        
        public void setStatus(SessionStatus status) {
            this.status = status;
        }
        
        public Integer getTotalVotes() {
            return totalVotes;
        }
        
        public void setTotalVotes(Integer totalVotes) {
            this.totalVotes = totalVotes;
        }
        
        public List<VoteInfoDTO> getVotes() {
            return votes;
        }
        
        public void setVotes(List<VoteInfoDTO> votes) {
            this.votes = votes;
        }
        
        // Classe interna para informações dos votos
        public static class VoteInfoDTO {
            private String userId;
            private String cpf;
            private String vote;
            
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            private LocalDateTime timestamp;
            
            // Construtor padrão
            public VoteInfoDTO() {}
            
            // Construtor com todos os campos
            public VoteInfoDTO(String userId, String cpf, String vote, LocalDateTime timestamp) {
                this.userId = userId;
                this.cpf = cpf;
                this.vote = vote;
                this.timestamp = timestamp;
            }
            
            // Getters e Setters
            public String getUserId() {
                return userId;
            }
            
            public void setUserId(String userId) {
                this.userId = userId;
            }
            
            public String getCpf() {
                return cpf;
            }
            
            public void setCpf(String cpf) {
                this.cpf = cpf;
            }
            
            public String getVote() {
                return vote;
            }
            
            public void setVote(String vote) {
                this.vote = vote;
            }
            
            public LocalDateTime getTimestamp() {
                return timestamp;
            }
            
            public void setTimestamp(LocalDateTime timestamp) {
                this.timestamp = timestamp;
            }
        }
    }
}
