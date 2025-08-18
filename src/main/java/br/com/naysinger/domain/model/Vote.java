package br.com.naysinger.domain.model;

import br.com.naysinger.common.enums.VoteType;

import java.time.LocalDateTime;

public class Vote {

    private String userId;
    private String cpf;
    private VoteType vote;
    private LocalDateTime timestamp;

    // Construtor privado - só pode ser chamado pelo Builder
    private Vote(Builder builder) {
        this.userId = builder.userId;
        this.cpf = builder.cpf;
        this.vote = builder.vote;
        this.timestamp = builder.timestamp;
    }

    // Método estático para criar um novo voto com timestamp automático
    public static Vote createNew(String userId, String cpf, VoteType vote) {
        return new Builder()
                .userId(userId)
                .cpf(cpf)
                .vote(vote)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Método para criar um builder a partir de uma instância existente
    public Builder toBuilder() {
        return new Builder()
                .userId(this.userId)
                .cpf(this.cpf)
                .vote(this.vote)
                .timestamp(this.timestamp);
    }

    // Metodo para obter um builder vazio
    public static Builder builder() {
        return new Builder();
    }

    // Getters (sem setters para imutabilidade)
    public String getUserId() {
        return userId;
    }

    public String getCpf() {
        return cpf;
    }

    public VoteType getVote() {
        return vote;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Classe Builder interna
    public static class Builder {
        private String userId;
        private String cpf;
        private VoteType vote;
        private LocalDateTime timestamp;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder cpf(String cpf) {
            this.cpf = cpf;
            return this;
        }

        public Builder vote(VoteType vote) {
            this.vote = vote;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        // Metodo de conveniência para definir timestamp como agora
        public Builder timestampNow() {
            this.timestamp = LocalDateTime.now();
            return this;
        }

        // Metodo de conveniência para voto SIM
        public Builder voteYes() {
            this.vote = VoteType.YES;
            return this;
        }

        // Metodo de conveniência para voto NÃO
        public Builder voteNo() {
            this.vote = VoteType.NO;
            return this;
        }

        public Vote build() {
            // Validações opcionais
            if (userId == null || userId.trim().isEmpty()) {
                throw new IllegalArgumentException("UserId não pode ser nulo ou vazio");
            }
            if (cpf == null || cpf.trim().isEmpty()) {
                throw new IllegalArgumentException("CPF não pode ser nulo ou vazio");
            }
            if (vote == null) {
                throw new IllegalArgumentException("Tipo de voto não pode ser nulo");
            }
            if (timestamp == null) {
                this.timestamp = LocalDateTime.now(); // Define timestamp automático se não fornecido
            }

            return new Vote(this);
        }
    }

    // Métodos utilitários para facilitar comparações e operações
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Vote vote = (Vote) obj;
        return userId.equals(vote.userId) && cpf.equals(vote.cpf);
    }

    @Override
    public int hashCode() {
        return userId.hashCode() + cpf.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Vote{userId='%s', cpf='%s', vote=%s, timestamp=%s}",
                userId, cpf, vote, timestamp);
    }
}