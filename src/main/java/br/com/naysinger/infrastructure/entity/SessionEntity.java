package br.com.naysinger.infrastructure.entity;

import br.com.naysinger.common.enums.SessionStatus;
import br.com.naysinger.common.enums.VoteType;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SessionEntity {
    
    @Field("session_id")
    private String sessionId;
    
    @Field("start_time")
    private LocalDateTime startTime;
    
    @Field("end_time")
    private LocalDateTime endTime;
    
    @Field("status")
    private SessionStatus status;
    
    @Field("votes")
    private List<VoteEntity> votes;
    
    // Construtor padrão
    public SessionEntity() {
        this.votes = new ArrayList<>();
    }
    
    // Construtor com todos os campos
    public SessionEntity(String sessionId, LocalDateTime startTime, LocalDateTime endTime, 
                        SessionStatus status, List<VoteEntity> votes) {
        this.sessionId = sessionId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.votes = votes != null ? votes : new ArrayList<>();
    }
    
    // Construtor para criar nova sessão
    public static SessionEntity createNew(LocalDateTime startTime, Integer durationMinutes) {
        String sessionId = "session_" + System.currentTimeMillis();
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);
        
        return new SessionEntity(
            sessionId,
            startTime,
            endTime,
            SessionStatus.OPEN,
            new ArrayList<>()
        );
    }
    
    // Método para adicionar voto
    public void addVote(VoteEntity vote) {
        if (this.votes == null) {
            this.votes = new ArrayList<>();
        }
        this.votes.add(vote);
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
    
    public List<VoteEntity> getVotes() {
        return votes;
    }
    
    public void setVotes(List<VoteEntity> votes) {
        this.votes = votes;
    }
}
