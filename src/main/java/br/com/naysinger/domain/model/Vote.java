package br.com.naysinger.domain.model;

import br.com.naysinger.common.enums.VoteType;

import java.time.LocalDateTime;

public class Vote {
    
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
