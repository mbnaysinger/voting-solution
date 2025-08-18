package br.com.naysinger.common.exception;

public class CpfNotFoundException extends RuntimeException {
	public CpfNotFoundException(String cpf) {
		super("CPF não encontrado ou não apto para votar");
	}
}
