package br.com.naysinger.domain.model;

import br.com.naysinger.common.enums.AgendaStatus;
import br.com.naysinger.common.enums.SessionStatus;
import br.com.naysinger.common.enums.VoteType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
            null,
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
    
    // Classe interna Session
    public static class Session {
        
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
            String sessionId = UUID.randomUUID().toString();
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
        
        // Classe interna Vote
        public static class Vote {
            
            private String userId;
            private String cpf;
            private VoteType vote;
            private LocalDateTime timestamp;
            
            // Construtor padrão
            public Vote() {}
            
            // Construtor com todos os campos
            public Vote(String userId, String cpf, VoteType vote, LocalDateTime timestamp) {
                this.userId = userId;
                this.cpf = cpf;
                this.vote = vote;
                this.timestamp = timestamp;
            }
            
            // Construtor para criar novo voto
            public Vote(String userId, String cpf, VoteType vote) {
                this.userId = userId;
                this.cpf = cpf;
                this.vote = vote;
                this.timestamp = LocalDateTime.now();
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
            
            public VoteType getVote() {
                return vote;
            }
            
            public void setVote(VoteType vote) {
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
