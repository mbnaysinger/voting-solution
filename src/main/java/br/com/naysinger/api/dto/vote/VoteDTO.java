package br.com.naysinger.api.dto.vote;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class VoteDTO {
    
    private String userId;
    private String cpf;
    private String vote;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    // Construtor padrão
    public VoteDTO() {}
    
    // Construtor com todos os campos
    public VoteDTO(String userId, String cpf, String vote, LocalDateTime timestamp) {
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
