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

        return agendaService.createAgenda(agendaMapper.toDomain(request))
            .map(agendaMapper::toResponse)
            .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar agenda por ID", 
               description = "Retorna os detalhes completos de uma agenda com suas sessões e votos")
    public Mono<ResponseEntity<AgendaResponseDTO>> getAgenda(@PathVariable String id) {
        return agendaService.findById(id)
            .map(agendaMapper::toResponse)
            .map(ResponseEntity::ok);
    }
    
    @GetMapping("/agenda/{agendaId}")
    @Operation(summary = "Buscar agenda por agendaId", 
               description = "Retorna os detalhes completos de uma agenda com suas sessões e votos")
    public Mono<ResponseEntity<AgendaResponseDTO>> getAgendaByAgendaId(@PathVariable String agendaId) {
        return agendaService.findByAgendaId(agendaId)
            .map(agendaMapper::toResponse)
            .map(ResponseEntity::ok);
    }
    
    @GetMapping("/session/{sessionId}")
    @Operation(summary = "Buscar agenda por sessionId", 
               description = "Retorna os detalhes completos de uma agenda através do ID da sessão")
    public Mono<ResponseEntity<AgendaResponseDTO>> getAgendaBySessionId(@PathVariable String sessionId) {
        return agendaService.findBySessionId(sessionId)
            .map(agendaMapper::toResponse)
            .map(ResponseEntity::ok);
    }
    
    @GetMapping
    @Operation(summary = "Listar todas as agendas", 
               description = "Retorna todas as agendas do sistema")
    public Mono<ResponseEntity<Flux<AgendaResponseDTO>>> getAllAgendas() {
        return agendaService.findAll()
            .collectList()
            .flatMap(agendas -> {
                if (agendas.isEmpty()) {
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
        return agendaService.findActiveSessions()
            .collectList()
            .flatMap(agendas -> {
                if (agendas.isEmpty()) {
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
        
        return agendaService.addSession(
                agendaId,
                request.getStartTime(),
                request.getDurationMinutes()
            )
            .map(agendaMapper::toResponse)
            .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }
    
    @PostMapping("/session/{sessionId}/close")
    @Operation(summary = "Fechar sessão", 
               description = "Fecha uma sessão de votação ativa")
    public Mono<ResponseEntity<AgendaResponseDTO>> closeSession(@PathVariable String sessionId) {
        return agendaService.closeSession(sessionId)
            .map(agendaMapper::toResponse)
            .map(ResponseEntity::ok);
    }
    
    @PostMapping("/{agendaId}/close")
    @Operation(summary = "Fechar agenda", 
               description = "Fecha uma agenda e todas as suas sessões abertas")
    public Mono<ResponseEntity<AgendaResponseDTO>> closeAgenda(@PathVariable String agendaId) {
        return agendaService.closeAgenda(agendaId)
            .map(agendaMapper::toResponse)
            .map(ResponseEntity::ok);
    }
    
    @PostMapping("/session/{sessionId}/vote")
    @Operation(summary = "Registrar voto", 
               description = "Registra um voto em uma sessão ativa")
    public Mono<ResponseEntity<String>> addVote(
            @PathVariable String sessionId,
            @Valid @RequestBody VoteRequestDTO request) {
        
        return agendaService.addVote(sessionId, request.getUserId(), request.getCpf(), request.getVoteType())
            .flatMap(agenda -> Mono.just(ResponseEntity.status(HttpStatus.CREATED).
                    body("Voto computado com sucesso, obrigado.")));
    }

    @GetMapping("/session/{sessionId}/result")
    @Operation(summary = "Obter resultado da votação",
            description = "Retorna o resultado final de uma sessão fechada")
    public Mono<ResponseEntity<Object>> getVoteResult(@PathVariable String sessionId) {
        return agendaService.findBySessionId(sessionId)
                .flatMap(agenda -> {
                    // Buscar a sessão específica na lista de sessões
                    Session session = agenda.getSessionById(sessionId);
                    
                    if (session == null || session.getStatus() != SessionStatus.CLOSED) {
                        return Mono.just(ResponseEntity.badRequest()
                                .body("Sessão ainda não foi fechada"));
                    }

                    VoteResult result = session.getVoteResult();

                    return Mono.just(ResponseEntity.ok(new VoteResultResponse(
                            result.simVotes(),
                            result.naoVotes(),
                            result.totalVotes(),
                            result.getWinner()
                    )));
                });
    }
}
