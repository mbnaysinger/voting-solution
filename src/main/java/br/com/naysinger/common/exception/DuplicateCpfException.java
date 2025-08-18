package br.com.naysinger.common.exception;

public class DuplicateCpfException extends BusinessException {
    
    public DuplicateCpfException(String cpf) {
        super("CPF " + cpf + " já possui voto registrado nesta sessão");
    }
    
    public DuplicateCpfException(String cpf, String message) {
        super(message);
    }
}
