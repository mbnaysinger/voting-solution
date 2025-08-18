package br.com.naysinger.api.controller.v1;

import br.com.naysinger.api.dto.AgendaRequestDTO;
import br.com.naysinger.api.dto.AgendaResponseDTO;
import br.com.naysinger.api.dto.session.SessionRequestDTO;
import br.com.naysinger.api.mapper.AgendaMapper;
import br.com.naysinger.common.enums.SessionStatus;
import br.com.naysinger.domain.model.AgendaCycle;
import br.com.naysinger.domain.model.Session;
import br.com.naysinger.domain.service.AgendaCycleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/agenda")
@Tag(name = "Agenda Cycles", description = "API para gerenciamento completo de agendas com sessões de votação")
public class AgendaCycleController {
    
    private final AgendaCycleService agendaCycleService;
    private final AgendaMapper agendaMapper;
    
    public AgendaCycleController(AgendaCycleService agendaCycleService, AgendaMapper agendaMapper) {
        this.agendaCycleService = agendaCycleService;
        this.agendaMapper = agendaMapper;
    }
    
    @PostMapping
    @Operation(summary = "Criar uma nova agenda com ou sem sessão", 
               description = "Cria uma nova agenda e opcionalmente uma sessão de votação")
    public Mono<ResponseEntity<AgendaResponseDTO>> createAgendaCycle(@Valid @RequestBody AgendaRequestDTO request) {
        AgendaCycle agendaCycle = agendaMapper.toDomain(request);
        
        return agendaCycleService.createAgendaCycle(agendaCycle)
            .map(agendaMapper::toResponse)
            .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar agenda por ID", 
               description = "Retorna os detalhes completos de uma agenda com sua sessão e votos")
    public Mono<ResponseEntity<AgendaResponseDTO>> getAgendaCycle(@PathVariable String id) {
        return agendaCycleService.findById(id)
            .map(agendaMapper::toResponse)
            .map(ResponseEntity::ok);
    }
    
    @GetMapping("/agenda/{agendaId}")
    @Operation(summary = "Buscar agenda por agendaId", 
               description = "Retorna os detalhes completos de uma agenda com sua sessão e votos")
    public Mono<ResponseEntity<AgendaResponseDTO>> getAgendaCycleByAgendaId(@PathVariable String agendaId) {
        return agendaCycleService.findByAgendaId(agendaId)
            .map(agendaMapper::toResponse)
            .map(ResponseEntity::ok);
    }
    
    @GetMapping("/session/{sessionId}")
    @Operation(summary = "Buscar agenda por sessionId", 
               description = "Retorna os detalhes completos de uma agenda através do ID da sessão")
    public Mono<ResponseEntity<AgendaResponseDTO>> getAgendaCycleBySessionId(@PathVariable String sessionId) {
        return agendaCycleService.findBySessionId(sessionId)
            .map(agendaMapper::toResponse)
            .map(ResponseEntity::ok);
    }
    
    @GetMapping
    @Operation(summary = "Listar todas as agendas", 
               description = "Retorna todas as agendas do sistema")
    public Mono<ResponseEntity<Flux<AgendaResponseDTO>>> getAllAgendaCycles() {
        return agendaCycleService.findAll()
            .collectList()
            .flatMap(agendaCycles -> {
                if (agendaCycles.isEmpty()) {
                    return Mono.just(ResponseEntity.ok(Flux.empty()));
                }
                
                Flux<AgendaResponseDTO> responseFlux = Flux.fromIterable(agendaCycles)
                    .map(agendaMapper::toResponse)
                    .filter(Objects::nonNull);
                
                return Mono.just(ResponseEntity.ok(responseFlux));
            });
    }
    
    @GetMapping("/active")
    @Operation(summary = "Listar agendas com sessões ativas", 
               description = "Retorna todas as agendas que possuem sessões de votação ativas")
    public Mono<ResponseEntity<Flux<AgendaResponseDTO>>> getActiveSessions() {
        return agendaCycleService.findActiveSessions()
            .collectList()
            .flatMap(agendaCycles -> {
                if (agendaCycles.isEmpty()) {
                    return Mono.just(ResponseEntity.ok(Flux.empty()));
                }
                
                Flux<AgendaResponseDTO> responseFlux = Flux.fromIterable(agendaCycles)
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
        
        return agendaCycleService.addSession(
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
        return agendaCycleService.closeSession(sessionId)
            .map(agendaMapper::toResponse)
            .map(ResponseEntity::ok);
    }
    
    @PostMapping("/session/{sessionId}/vote")
    @Operation(summary = "Registrar voto", 
               description = "Registra um voto em uma sessão ativa")
    public Mono<ResponseEntity<AgendaResponseDTO>> addVote(
            @PathVariable String sessionId,
            @RequestParam String userId,
            @RequestParam String cpf,
            @RequestParam String voteType) {
        
        return agendaCycleService.addVote(sessionId, userId, cpf, voteType)
            .map(agendaMapper::toResponse)
            .map(ResponseEntity::ok);
    }
    
    @GetMapping("/session/{sessionId}/result")
    @Operation(summary = "Obter resultado da votação", 
               description = "Retorna o resultado final de uma sessão fechada")
    public Mono<ResponseEntity<Object>> getVoteResult(@PathVariable String sessionId) {
        return agendaCycleService.findBySessionId(sessionId)
            .flatMap(agendaCycle -> {
                if (agendaCycle.getSession() == null || 
                    agendaCycle.getSession().getStatus() != SessionStatus.CLOSED) {
                    return Mono.just(ResponseEntity.badRequest()
                        .body("Sessão ainda não foi fechada"));
                }
                
                Session.VoteResult result = agendaCycle.getSession().getVoteResult();
                
                return Mono.just(ResponseEntity.ok(new VoteResultResponse(
                    result.getSimVotes(),
                    result.getNaoVotes(),
                    result.getTotalVotes(),
                    result.getWinner()
                )));
            });
    }
    
    // Classe interna para resposta do resultado da votação
    private static class VoteResultResponse {
        private final long simVotes;
        private final long naoVotes;
        private final long totalVotes;
        private final String winner;
        
        public VoteResultResponse(long simVotes, long naoVotes, long totalVotes, String winner) {
            this.simVotes = simVotes;
            this.naoVotes = naoVotes;
            this.totalVotes = totalVotes;
            this.winner = winner;
        }
        
        public long getSimVotes() { return simVotes; }
        public long getNaoVotes() { return naoVotes; }
        public long getTotalVotes() { return totalVotes; }
        public String getWinner() { return winner; }
    }
}
