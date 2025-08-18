package br.com.naysinger.api.dto.session;

import br.com.naysinger.api.dto.vote.VoteDTO;
import br.com.naysinger.common.enums.SessionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class SessionDTO {
    
    private String sessionId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    
    private SessionStatus status;
    private Integer totalVotes;
    private List<VoteDTO> votes;
    
    // Construtor padrão
    public SessionDTO() {}
    
    // Construtor com todos os campos
    public SessionDTO(String sessionId, LocalDateTime startTime, LocalDateTime endTime, 
                     SessionStatus status, Integer totalVotes, List<VoteDTO> votes) {
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
    
    public List<VoteDTO> getVotes() {
        return votes;
    }
    
    public void setVotes(List<VoteDTO> votes) {
        this.votes = votes;
    }
}
