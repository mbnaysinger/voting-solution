package br.com.naysinger.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class AddSessionRequestDTO {
    
    @NotNull(message = "A data de início é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    
    @NotNull(message = "A duração em minutos é obrigatória")
    @Positive(message = "A duração deve ser maior que zero")
    private Integer durationMinutes;
    
    // Construtor padrão
    public AddSessionRequestDTO() {}
    
    // Construtor com todos os campos
    public AddSessionRequestDTO(LocalDateTime startTime, Integer durationMinutes) {
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
    }
    
    // Getters e Setters
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public Integer getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
