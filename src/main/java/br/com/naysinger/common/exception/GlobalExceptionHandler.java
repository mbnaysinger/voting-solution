package br.com.naysinger.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(WebExchangeBindException.class)
	public Mono<ResponseEntity<Map<String, Object>>> handleValidationExceptions(WebExchangeBindException ex) {
		Map<String, Object> errors = new HashMap<>();
		errors.put("timestamp", LocalDateTime.now());
		errors.put("status", HttpStatus.BAD_REQUEST.value());
		errors.put("error", "Erro de validação");
		errors.put("message", "Dados de entrada inválidos");
		Map<String, String> fieldErrors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));
		errors.put("fieldErrors", fieldErrors);
		return Mono.just(ResponseEntity.badRequest().body(errors));
	}
	
	@ExceptionHandler(BusinessException.class)
	public Mono<ResponseEntity<Map<String, Object>>> handleBusinessException(BusinessException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("error", "Erro de negócio");
		body.put("message", ex.getMessage());
		return Mono.just(ResponseEntity.badRequest().body(body));
	}
	
	@ExceptionHandler(DuplicateCpfException.class)
	public Mono<ResponseEntity<Map<String, Object>>> handleDuplicateCpfException(DuplicateCpfException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.CONFLICT.value());
		body.put("error", "CPF duplicado");
		body.put("message", ex.getMessage());
		return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(body));
	}
	
	@ExceptionHandler(CpfNotFoundException.class)
	public Mono<ResponseEntity<Map<String, Object>>> handleCpfNotFoundException(CpfNotFoundException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.NOT_FOUND.value());
		body.put("error", "CPF não apto ou inválido");
		body.put("message", ex.getMessage());
		return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(body));
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public Mono<ResponseEntity<Map<String, Object>>> handleIllegalArgumentException(IllegalArgumentException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.BAD_REQUEST.value());
		body.put("error", "Argumento inválido");
		body.put("message", ex.getMessage());
		return Mono.just(ResponseEntity.badRequest().body(body));
	}
	
	@ExceptionHandler(Exception.class)
	public Mono<ResponseEntity<Map<String, Object>>> handleGenericException() {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
		body.put("error", "Erro interno do servidor");
		body.put("message", "Ocorreu um erro inesperado");
		return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body));
	}
}
