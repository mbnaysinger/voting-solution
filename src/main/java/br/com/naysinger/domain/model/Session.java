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
    
    // Construtor padrão
    public Session() {
        this.votes = new ArrayList<>();
    }
    
    // Construtor com todos os campos
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
    
    // Método para adicionar voto
    public void addVote(String userId, String cpf, VoteType voteType) {
        Vote vote = new Vote(userId, cpf, voteType);
        this.votes.add(vote);
    }
    
    // Método para verificar se a sessão está ativa
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return status == SessionStatus.OPEN && 
               now.isAfter(startTime) && 
               now.isBefore(endTime);
    }
    
    // Método para fechar a sessão
    public void closeSession() {
        this.status = SessionStatus.CLOSED;
    }
    
    // Método para obter total de votos
    public int getTotalVotes() {
        return votes != null ? votes.size() : 0;
    }
    
    // Método para obter resultado da votação
    public VoteResult getVoteResult() {
        if (votes == null || votes.isEmpty()) {
            return new VoteResult(0, 0, 0);
        }
        
        long simVotes = votes.stream().filter(v -> v.getVote() == VoteType.YES).count();
        long naoVotes = votes.stream().filter(v -> v.getVote() == VoteType.NO).count();
        
        return new VoteResult(simVotes, naoVotes, votes.size());
    }
    
    // Getters e Setters
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
    
    // Classe interna para resultado da votação
    public static class VoteResult {
        private final long simVotes;
        private final long naoVotes;
        private final long totalVotes;
        
        public VoteResult(long simVotes, long naoVotes, long totalVotes) {
            this.simVotes = simVotes;
            this.naoVotes = naoVotes;
            this.totalVotes = totalVotes;
        }
        
        public long getSimVotes() { return simVotes; }
        public long getNaoVotes() { return naoVotes; }
        public long getTotalVotes() { return totalVotes; }
        
        public String getWinner() {
            if (simVotes > naoVotes) return "SIM";
            if (naoVotes > simVotes) return "NÃO";
            return "EMPATE";
        }
    }
}
