package br.com.naysinger.infrastructure.entity;

import br.com.naysinger.common.enums.VoteType;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

public class VoteEntity {

    @Field("user_id")
    private String userId;

    private String cpf;

    private VoteType vote;

    @Field("timestamp")
    private LocalDateTime timestamp;

    public VoteEntity() {}

    public VoteEntity(String userId, String cpf, VoteType vote) {
        this.userId = userId;
        this.cpf = cpf;
        this.vote = vote;
        this.timestamp = LocalDateTime.now();
    }

    public VoteEntity(String userId, String cpf, VoteType vote, LocalDateTime timestamp) {
        this.userId = userId;
        this.cpf = cpf;
        this.vote = vote;
        this.timestamp = timestamp;
    }

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

    public void prePersist() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "Vote{" +
                "userId='" + userId + '\'' +
                ", cpf='" + cpf + '\'' +
                ", vote=" + vote +
                ", timestamp=" + timestamp +
                '}';
    }
}
