package br.com.naysinger.api.dto.vote;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class VoteRequestDTO {
    
    @NotBlank(message = "O ID do usuário é obrigatório")
    private String userId;
    
    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter exatamente 11 dígitos")
    private String cpf;
    
    @NotBlank(message = "O tipo de voto é obrigatório")
    @Pattern(regexp = "^(YES|NO)$", message = "Tipo de voto deve ser 'YES' ou 'NO'")
    private String voteType;
    
    // Construtor padrão
    public VoteRequestDTO() {}
    
    // Construtor com todos os campos
    public VoteRequestDTO(String userId, String cpf, String voteType) {
        this.userId = userId;
        this.cpf = cpf;
        this.voteType = voteType;
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
    
    public String getVoteType() {
        return voteType;
    }
    
    public void setVoteType(String voteType) {
        this.voteType = voteType;
    }
}
