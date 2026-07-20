package com.orquestador.orchestrator.api;

import com.orquestador.orchestrator.api.dtos.AgentCreateInput;
import com.orquestador.orchestrator.api.dtos.AgentResponse;
import com.orquestador.orchestrator.application.OrchestratorService;
import com.orquestador.orchestrator.domain.Agent;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/agents")
@CrossOrigin(origins = "*")
public class AgentController {

    private final OrchestratorService orchestratorService;

    public AgentController(OrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @PostMapping
    public ResponseEntity<AgentResponse> registerAgent(@Valid @RequestBody AgentCreateInput input) {
        Agent agent = orchestratorService.createAgent(
                input.getName(),
                input.getRole(),
                input.getSystemPrompt(),
                input.getModelName(),
                input.getTemperature()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(AgentResponse.fromDomain(agent));
    }

    @GetMapping
    public ResponseEntity<List<AgentResponse>> listAgents() {
        List<AgentResponse> responses = orchestratorService.listAgents()
                .stream()
                .map(AgentResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
