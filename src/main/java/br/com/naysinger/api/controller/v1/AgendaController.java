package br.com.naysinger.api.controller.v1;

import br.com.naysinger.api.dto.AgendaResponse;
import br.com.naysinger.api.dto.CreateAgendaRequest;
import br.com.naysinger.api.mapper.AgendaMapper;
import br.com.naysinger.domain.service.AgendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/agendas")
@Tag(name = "Agendas", description = "API para gerenciamento de pautas de votação")
public class AgendaController {
    
    private final AgendaService agendaService;
    private final AgendaMapper agendaMapper;
    
    public AgendaController(AgendaService agendaService, AgendaMapper agendaMapper) {
        this.agendaService = agendaService;
        this.agendaMapper = agendaMapper;
    }
    
    @PostMapping
    @Operation(summary = "Criar nova pauta", description = "Cria uma nova pauta de votação")
    public Mono<ResponseEntity<AgendaResponse>> createAgenda(@Valid @RequestBody CreateAgendaRequest request) {
        return agendaService.createAgenda(agendaMapper.toModel(request))
                .map(agendaMapper::toDto)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .onErrorReturn(ResponseEntity.badRequest().build());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar pauta por ID", description = "Busca uma pauta específica pelo seu ID")
    public Mono<ResponseEntity<AgendaResponse>> getAgendaById(@PathVariable String id) {
        return agendaService.findAgendaById(id)
                .map(agendaMapper::toDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/agenda-id/{agendaId}")
    @Operation(summary = "Buscar pauta por agendaId", description = "Busca uma pauta específica pelo seu agendaId")
    public Mono<ResponseEntity<AgendaResponse>> getAgendaByAgendaId(@PathVariable String agendaId) {
        return agendaService.findAgendaByAgendaId(agendaId)
                .map(agendaMapper::toDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
