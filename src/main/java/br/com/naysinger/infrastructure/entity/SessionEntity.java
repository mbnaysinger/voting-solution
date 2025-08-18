package br.com.naysinger.infrastructure.entity;

import br.com.naysinger.common.enums.SessionStatus;
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

    private SessionStatus status;

    private List<VoteEntity> votes;

    public SessionEntity() {
        this.votes = new ArrayList<>();
    }

    public SessionEntity(String sessionId, LocalDateTime startTime, SessionStatus status) {
        this.sessionId = sessionId;
        this.startTime = startTime;
        this.status = status;
        this.votes = new ArrayList<>();
    }

    public SessionEntity(String sessionId, LocalDateTime startTime, LocalDateTime endTime, SessionStatus status, List<VoteEntity> votes) {
        this.sessionId = sessionId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.votes = votes != null ? votes : new ArrayList<>();
    }

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
        this.votes = votes != null ? votes : new ArrayList<>();
    }

    public void addVote(VoteEntity vote) {
        if (this.votes == null) {
            this.votes = new ArrayList<>();
        }
        this.votes.add(vote);
    }

    public int getTotalVotes() {
        return votes != null ? votes.size() : 0;
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionId='" + sessionId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                ", votes=" + votes +
                '}';
    }
}
