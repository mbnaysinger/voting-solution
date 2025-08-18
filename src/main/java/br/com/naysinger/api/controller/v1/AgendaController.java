package br.com.naysinger.api.controller.v1;

import br.com.naysinger.api.dto.AgendaRequestDTO;
import br.com.naysinger.api.dto.AgendaResponseDTO;
import br.com.naysinger.api.dto.vote.VoteResultResponse;
import br.com.naysinger.api.dto.session.SessionRequestDTO;
import br.com.naysinger.api.dto.vote.VoteRequestDTO;
import br.com.naysinger.api.mapper.AgendaMapper;
import br.com.naysinger.common.enums.SessionStatus;
import br.com.naysinger.domain.model.Session;
import br.com.naysinger.domain.model.VoteResult;
import br.com.naysinger.domain.service.AgendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/agenda")
@Tag(name = "Agenda Cycles", description = "API para gerenciamento completo de agendas com sessões de votação")
public class AgendaController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AgendaController.class);
	
	private final AgendaService agendaService;
	private final AgendaMapper agendaMapper;
	
	public AgendaController(AgendaService agendaService, AgendaMapper agendaMapper) {
		this.agendaService = agendaService;
		this.agendaMapper = agendaMapper;
	}
	
	@PostMapping
	@Operation(summary = "Criar uma nova agenda com ou sem sessão", 
	           description = "Cria uma nova agenda e opcionalmente uma sessão de votação")
	public Mono<ResponseEntity<AgendaResponseDTO>> createAgenda(@Valid @RequestBody AgendaRequestDTO request) {
		LOGGER.info("[createAgenda] Iniciando criação de agenda. title={}, createdBy={}", request.getTitle(), request.getCreatedBy());
		return agendaService.createAgenda(agendaMapper.toDomain(request))
			.doOnSuccess(a -> LOGGER.info("[createAgenda] Agenda criada com sucesso. agendaId={}", a.getAgendaId()))
			.doOnError(e -> LOGGER.error("[createAgenda] Erro ao criar agenda. title={}", request.getTitle(), e))
			.map(agendaMapper::toResponse)
			.map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
	}
	
	@GetMapping("/{id}")
	@Operation(summary = "Buscar agenda por ID", 
	           description = "Retorna os detalhes completos de uma agenda com suas sessões e votos")
	public Mono<ResponseEntity<AgendaResponseDTO>> getAgenda(@PathVariable String id) {
		LOGGER.info("[getAgenda] Buscando agenda por id={}", id);
		return agendaService.findById(id)
			.doOnError(e -> LOGGER.error("[getAgenda] Erro ao buscar agenda. id={}", id, e))
			.map(agendaMapper::toResponse)
			.map(ResponseEntity::ok);
	}
	
	@GetMapping("/agenda/{agendaId}")
	@Operation(summary = "Buscar agenda por agendaId", 
	           description = "Retorna os detalhes completos de uma agenda com suas sessões e votos")
	public Mono<ResponseEntity<AgendaResponseDTO>> getAgendaByAgendaId(@PathVariable String agendaId) {
		LOGGER.info("[getAgendaByAgendaId] Buscando agenda por agendaId={}", agendaId);
		return agendaService.findByAgendaId(agendaId)
			.doOnError(e -> LOGGER.error("[getAgendaByAgendaId] Erro ao buscar agenda. agendaId={}", agendaId, e))
			.map(agendaMapper::toResponse)
			.map(ResponseEntity::ok);
	}
	
	@GetMapping("/session/{sessionId}")
	@Operation(summary = "Buscar agenda por sessionId", 
	           description = "Retorna os detalhes completos de uma agenda através do ID da sessão")
	public Mono<ResponseEntity<AgendaResponseDTO>> getAgendaBySessionId(@PathVariable String sessionId) {
		LOGGER.info("[getAgendaBySessionId] Buscando agenda por sessionId={}", sessionId);
		return agendaService.findBySessionId(sessionId)
			.doOnError(e -> LOGGER.error("[getAgendaBySessionId] Erro ao buscar por sessionId. sessionId={}", sessionId, e))
			.map(agendaMapper::toResponse)
			.map(ResponseEntity::ok);
	}
	
	@GetMapping
	@Operation(summary = "Listar todas as agendas", 
	           description = "Retorna todas as agendas do sistema")
	public Mono<ResponseEntity<Flux<AgendaResponseDTO>>> getAllAgendas() {
		LOGGER.info("[getAllAgendas] Listando agendas");
		return agendaService.findAll()
			.collectList()
			.doOnError(e -> LOGGER.error("[getAllAgendas] Erro ao listar agendas", e))
			.flatMap(agendas -> {
				if (agendas.isEmpty()) {
					LOGGER.info("[getAllAgendas] Nenhuma agenda encontrada");
					return Mono.just(ResponseEntity.ok(Flux.empty()));
				}
				Flux<AgendaResponseDTO> responseFlux = Flux.fromIterable(agendas)
					.map(agendaMapper::toResponse)
					.filter(Objects::nonNull);
				return Mono.just(ResponseEntity.ok(responseFlux));
			});
	}
	
	@GetMapping("/active")
	@Operation(summary = "Listar agendas com sessões ativas", 
	           description = "Retorna todas as agendas que possuem sessões de votação ativas")
	public Mono<ResponseEntity<Flux<AgendaResponseDTO>>> getActiveSessions() {
		LOGGER.info("[getActiveSessions] Listando agendas com sessões ativas");
		return agendaService.findActiveSessions()
			.collectList()
			.doOnError(e -> LOGGER.error("[getActiveSessions] Erro ao listar sessões ativas", e))
			.flatMap(agendas -> {
				if (agendas.isEmpty()) {
					LOGGER.info("[getActiveSessions] Nenhuma agenda com sessão ativa encontrada");
					return Mono.just(ResponseEntity.ok(Flux.empty()));
				}
				Flux<AgendaResponseDTO> responseFlux = Flux.fromIterable(agendas)
					.map(agendaMapper::toResponse)
					.filter(Objects::nonNull);
				return Mono.just(ResponseEntity.ok(responseFlux));
			});
	}
	
	@PostMapping("/{agendaId}/sessions")
	@Operation(summary = "Criar sessão para agenda existente", 
	           description = "Cria uma nova sessão de votação para uma agenda já existente")
	public Mono<ResponseEntity<AgendaResponseDTO>> createSessionForAgenda(
			@PathVariable String agendaId,
			@Valid @RequestBody SessionRequestDTO request) {
		LOGGER.info("[createSessionForAgenda] Criando sessão. agendaId={}, startTime={}, durationMinutes={}", agendaId, request.getStartTime(), request.getDurationMinutes());
		return agendaService.addSession(
				agendaId,
				request.getStartTime(),
				request.getDurationMinutes()
			)
			.doOnSuccess(a -> LOGGER.info("[createSessionForAgenda] Sessão criada com sucesso. agendaId={}, sessionsCount={}", agendaId, a.getSessions() != null ? a.getSessions().size() : 0))
			.doOnError(e -> LOGGER.error("[createSessionForAgenda] Erro ao criar sessão. agendaId={}", agendaId, e))
			.map(agendaMapper::toResponse)
			.map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
	}
	
	@PostMapping("/session/{sessionId}/close")
	@Operation(summary = "Fechar sessão", 
	           description = "Fecha uma sessão de votação ativa")
	public Mono<ResponseEntity<AgendaResponseDTO>> closeSession(@PathVariable String sessionId) {
		LOGGER.info("[closeSession] Fechando sessão. sessionId={}", sessionId);
		return agendaService.closeSession(sessionId)
			.doOnSuccess(a -> LOGGER.info("[closeSession] Sessão fechada com sucesso. sessionId={}", sessionId))
			.doOnError(e -> LOGGER.error("[closeSession] Erro ao fechar sessão. sessionId={}", sessionId, e))
			.map(agendaMapper::toResponse)
			.map(ResponseEntity::ok);
	}
	
	@PostMapping("/{agendaId}/close")
	@Operation(summary = "Fechar agenda", 
	           description = "Fecha uma agenda e todas as suas sessões abertas")
	public Mono<ResponseEntity<AgendaResponseDTO>> closeAgenda(@PathVariable String agendaId) {
		LOGGER.info("[closeAgenda] Fechando agenda. agendaId={}", agendaId);
		return agendaService.closeAgenda(agendaId)
			.doOnSuccess(a -> LOGGER.info("[closeAgenda] Agenda fechada com sucesso. agendaId={}", agendaId))
			.doOnError(e -> LOGGER.error("[closeAgenda] Erro ao fechar agenda. agendaId={}", agendaId, e))
			.map(agendaMapper::toResponse)
			.map(ResponseEntity::ok);
	}
	
	@PostMapping("/session/{sessionId}/vote")
	@Operation(summary = "Registrar voto", 
	           description = "Registra um voto em uma sessão ativa")
	public Mono<ResponseEntity<String>> addVote(
			@PathVariable String sessionId,
			@Valid @RequestBody VoteRequestDTO request) {
		String maskedCpf = request.getCpf() != null && request.getCpf().length() >= 4 ? "***********".substring(0, Math.max(0, request.getCpf().length() - 4)) + request.getCpf().substring(request.getCpf().length() - 4) : "***";
		LOGGER.info("[addVote] Registrando voto. sessionId={}, userId={}, cpfMasked={}, voteType={}", sessionId, request.getUserId(), maskedCpf, request.getVoteType());
		return agendaService.addVote(sessionId, request.getUserId(), request.getCpf(), request.getVoteType())
			.doOnSuccess(a -> LOGGER.info("[addVote] Voto computado com sucesso. sessionId={}, userId={}", sessionId, request.getUserId()))
			.doOnError(e -> LOGGER.warn("[addVote] Falha ao registrar voto. sessionId={}, userId={}", sessionId, request.getUserId(), e))
			.flatMap(agenda -> Mono.just(ResponseEntity.status(HttpStatus.CREATED).
					body("Voto computado com sucesso, obrigado.")));
	}

	@GetMapping("/session/{sessionId}/result")
	@Operation(summary = "Obter resultado da votação",
	        description = "Retorna o resultado final de uma sessão fechada")
	public Mono<ResponseEntity<Object>> getVoteResult(@PathVariable String sessionId) {
		LOGGER.info("[getVoteResult] Solicitando resultado da votação. sessionId={}", sessionId);
		return agendaService.findBySessionId(sessionId)
			.doOnError(e -> LOGGER.error("[getVoteResult] Erro ao obter sessão para resultado. sessionId={}", sessionId, e))
			.flatMap(agenda -> {
				Session session = agenda.getSessionById(sessionId);
				if (session == null || session.getStatus() != SessionStatus.CLOSED) {
					LOGGER.warn("[getVoteResult] Sessão não fechada ou inexistente. sessionId={}", sessionId);
					return Mono.just(ResponseEntity.badRequest().body("Sessão ainda não foi fechada"));
				}
				VoteResult result = session.getVoteResult();
				LOGGER.info("[getVoteResult] Resultado consolidado. sessionId={}, totalVotes={}", sessionId, result.totalVotes());
				return Mono.just(ResponseEntity.ok(new VoteResultResponse(
						result.simVotes(),
						result.naoVotes(),
						result.totalVotes(),
						result.getWinner()
				)));
			});
	}
}
