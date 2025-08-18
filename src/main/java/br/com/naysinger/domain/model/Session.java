package br.com.naysinger.domain.model;

import br.com.naysinger.common.enums.SessionStatus;
import br.com.naysinger.common.enums.VoteType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Session {
    
    private String sessionId;
    private String agendaId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SessionStatus status;
    private List<Vote> votes;
    
    public Session() {
        this.votes = new ArrayList<>();
    }
    
    public Session(String sessionId, String agendaId, LocalDateTime startTime,
                  LocalDateTime endTime, SessionStatus status, List<Vote> votes) {
        this.sessionId = sessionId;
        this.agendaId = agendaId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.votes = votes != null ? votes : new ArrayList<>();
    }
    
    // Construtor para criar nova sessão
    public static Session createNew(String agendaId, LocalDateTime startTime, Integer durationMinutes) {
        String sessionId = "session_" + UUID.randomUUID().toString().substring(0, 8);
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);
        
        return new Session(
            sessionId,
            agendaId,
            startTime,
            endTime,
            SessionStatus.OPEN,
            new ArrayList<>()
        );
    }
    
    // Metodo para adicionar voto
    public void addVote(String userId, String cpf, VoteType voteType) {
        Vote vote = new Vote(userId, cpf, voteType);
        this.votes.add(vote);
    }
    
    // Metodo para verificar se a sessão está ativa
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return status == SessionStatus.OPEN && 
               now.isAfter(startTime) && 
               now.isBefore(endTime);
    }
    
    // Metodo para fechar a sessao
    public void closeSession() {
        this.status = SessionStatus.CLOSED;
    }
    
    // Metodo para obter total de votos
    public int getTotalVotes() {
        return votes != null ? votes.size() : 0;
    }
    
    // Metodo para obter resultado da votacao
    public VoteResult getVoteResult() {
        if (votes == null || votes.isEmpty()) {
            return new VoteResult(0, 0, 0);
        }
        
        long simVotes = votes.stream().filter(v -> v.getVote() == VoteType.YES).count();
        long naoVotes = votes.stream().filter(v -> v.getVote() == VoteType.NO).count();
        
        return new VoteResult(simVotes, naoVotes, votes.size());
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getAgendaId() {
        return agendaId;
    }
    
    public void setAgendaId(String agendaId) {
        this.agendaId = agendaId;
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
    
    public List<Vote> getVotes() {
        return votes;
    }
    
    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }
}
