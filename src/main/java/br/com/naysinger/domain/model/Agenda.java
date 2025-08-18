package br.com.naysinger.domain.model;

import br.com.naysinger.common.enums.AgendaStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Agenda {

    private String id;
    private String agendaId;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private AgendaStatus status;
    private List<Session> sessions;
    private String createdBy;

    // Construtor privado - só pode ser chamado pelo Builder
    private Agenda(Builder builder) {
        this.id = builder.id;
        this.agendaId = builder.agendaId;
        this.title = builder.title;
        this.description = builder.description;
        this.createdAt = builder.createdAt;
        this.status = builder.status;
        this.sessions = builder.sessions != null ? builder.sessions : new ArrayList<>();
        this.createdBy = builder.createdBy;
    }

    // Metodo estático para criar uma nova agenda com valores padrão
    public static Agenda createNew(String agendaId, String title, String description, String createdBy) {
        return new Builder()
                .agendaId(agendaId)
                .title(title)
                .description(description)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .status(AgendaStatus.PENDING)
                .sessions(new ArrayList<>())
                .build();
    }

    // Metodo para criar um builder a partir de uma instância existente
    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .agendaId(this.agendaId)
                .title(this.title)
                .description(this.description)
                .createdAt(this.createdAt)
                .status(this.status)
                .sessions(new ArrayList<>(this.sessions))
                .createdBy(this.createdBy);
    }

    // Metodo para obter um builder vazio
    public static Builder builder() {
        return new Builder();
    }

    // Metodo para adicionar sessão
    public void addSession(LocalDateTime startTime, Integer durationMinutes) {
        Session newSession = Session.createNew(startTime, durationMinutes);
        this.sessions.add(newSession);
    }

    // Metodo para obter sessões
    public List<Session> getSessions() {
        return sessions;
    }

    // Metodo para verificar se tem sessão ativa
    public boolean hasActiveSession() {
        return sessions != null && sessions.stream().anyMatch(Session::isActive);
    }

    // Metodo para obter sessão por ID
    public Session getSessionById(String sessionId) {
        if (sessions == null) return null;
        return sessions.stream()
                .filter(s -> s.getSessionId().equals(sessionId))
                .findFirst()
                .orElse(null);
    }

    // Getters (sem setters para imutabilidade)
    public String getId() {
        return id;
    }

    public String getAgendaId() {
        return agendaId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public AgendaStatus getStatus() {
        return status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    // Classe Builder interna
    public static class Builder {
        private String id;
        private String agendaId;
        private String title;
        private String description;
        private LocalDateTime createdAt;
        private AgendaStatus status;
        private List<Session> sessions;
        private String createdBy;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder agendaId(String agendaId) {
            this.agendaId = agendaId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder status(AgendaStatus status) {
            this.status = status;
            return this;
        }

        public Builder sessions(List<Session> sessions) {
            this.sessions = sessions;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        // Metodo para adicionar uma sessão individual
        public Builder addSession(Session session) {
            if (this.sessions == null) {
                this.sessions = new ArrayList<>();
            }
            this.sessions.add(session);
            return this;
        }

        public Agenda build() {
            return new Agenda(this);
        }
    }
}