package br.com.naysinger.domain.model;

import br.com.naysinger.common.enums.SessionStatus;
import br.com.naysinger.common.enums.VoteType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Session {

    private String sessionId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SessionStatus status;
    private List<Vote> votes;

    // Construtor privado - só pode ser chamado pelo Builder
    private Session(Builder builder) {
        this.sessionId = builder.sessionId;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.status = builder.status;
        this.votes = builder.votes != null ? builder.votes : new ArrayList<>();
    }

    // Metodo estático para criar uma nova sessão com valores padrão
    public static Session createNew(LocalDateTime startTime, Integer durationMinutes) {
        String sessionId = "session_" + UUID.randomUUID().toString().substring(0, 8);
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);

        return new Builder()
                .sessionId(sessionId)
                .startTime(startTime)
                .endTime(endTime)
                .status(SessionStatus.OPEN)
                .votes(new ArrayList<>())
                .build();
    }

    // Metodo para criar um builder a partir de uma instância existente
    public Builder toBuilder() {
        return new Builder()
                .sessionId(this.sessionId)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .status(this.status)
                .votes(new ArrayList<>(this.votes));
    }

    // Metodo para obter um builder vazio
    public static Builder builder() {
        return new Builder();
    }

    public void addVote(String userId, String cpf, VoteType voteType) {
        Vote vote = Vote.builder()
                .userId(userId)
                .cpf(cpf)
                .vote(voteType)
                .build();
        this.votes.add(vote);
    }

    // Metodo para verificar se a sessão está ativa
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return status == SessionStatus.OPEN &&
                now.isBefore(endTime); // Sessão ativa se não expirou
    }

    // Metodo para verificar se a sessão já começou
    public boolean hasStarted() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime);
    }

    // Metodo para verificar se a sessão está em andamento (já começou e não expirou)
    public boolean isInProgress() {
        LocalDateTime now = LocalDateTime.now();
        return status == SessionStatus.OPEN &&
                now.isAfter(startTime) &&
                now.isBefore(endTime);
    }

    // Metodo para fechar a sessão
    public void closeSession() {
        this.status = SessionStatus.CLOSED;
    }

    // Metodo para obter total de votos
    public int getTotalVotes() {
        return votes != null ? votes.size() : 0;
    }

    // Metodo para obter resultado da votação
    public VoteResult getVoteResult() {
        if (votes == null || votes.isEmpty()) {
            return new VoteResult(0, 0, 0);
        }

        long simVotes = votes.stream().filter(v -> v.getVote() == VoteType.YES).count();
        long naoVotes = votes.stream().filter(v -> v.getVote() == VoteType.NO).count();

        return new VoteResult(simVotes, naoVotes, votes.size());
    }

    // Getters (sem setters para imutabilidade)
    public String getSessionId() {
        return sessionId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    // Classe Builder interna
    public static class Builder {
        private String sessionId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private SessionStatus status;
        private List<Vote> votes;

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder status(SessionStatus status) {
            this.status = status;
            return this;
        }

        public Builder votes(List<Vote> votes) {
            this.votes = votes;
            return this;
        }

        // Metodo para adicionar um voto individual
        public Builder addVote(Vote vote) {
            if (this.votes == null) {
                this.votes = new ArrayList<>();
            }
            this.votes.add(vote);
            return this;
        }

        // Metodo para adicionar voto com parâmetros
        public Builder addVote(String userId, String cpf, VoteType voteType) {
            if (this.votes == null) {
                this.votes = new ArrayList<>();
            }

            Vote vote = Vote.builder()
                    .userId(userId)
                    .cpf(cpf)
                    .vote(voteType)
                    .build();

            this.votes.add(vote);
            return this;
        }

        // Metodo de conveniência para definir duração em minutos
        public Builder duration(LocalDateTime startTime, Integer durationMinutes) {
            this.startTime = startTime;
            this.endTime = startTime.plusMinutes(durationMinutes);
            return this;
        }

        // Metodo de conveniência para gerar ID automático
        public Builder generateSessionId() {
            this.sessionId = "session_" + UUID.randomUUID().toString().substring(0, 8);
            return this;
        }

        public Session build() {
            return new Session(this);
        }
    }
}