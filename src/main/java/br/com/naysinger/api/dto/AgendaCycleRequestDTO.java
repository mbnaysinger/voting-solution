package br.com.naysinger.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class AgendaCycleRequestDTO {
    
    @NotBlank(message = "O título é obrigatório")
    private String title;
    
    private String description;
    
    private String createdBy;
    
    // Dados da Sessão (opcional - pode ser criada depois)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sessionStartTime;
    
    @Positive(message = "A duração deve ser maior que zero")
    private Integer sessionDurationMinutes;
    
    // Construtor padrão
    public AgendaCycleRequestDTO() {}
    
    // Construtor com todos os campos
    public AgendaCycleRequestDTO(String title, String description, String createdBy,
                                LocalDateTime sessionStartTime, Integer sessionDurationMinutes) {
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.sessionStartTime = sessionStartTime;
        this.sessionDurationMinutes = sessionDurationMinutes;
    }
    
    // Getters e Setters
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
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getSessionStartTime() {
        return sessionStartTime;
    }
    
    public void setSessionStartTime(LocalDateTime sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }
    
    public Integer getSessionDurationMinutes() {
        return sessionDurationMinutes;
    }
    
    public void setSessionDurationMinutes(Integer sessionDurationMinutes) {
        this.sessionDurationMinutes = sessionDurationMinutes;
    }
}
