package com.orquestador.orchestrator.api;

import com.orquestador.orchestrator.api.dtos.ExecutionCreateInput;
import com.orquestador.orchestrator.api.dtos.ExecutionResponse;
import com.orquestador.orchestrator.application.OrchestratorService;
import com.orquestador.orchestrator.domain.Execution;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ExecutionController {

    private final OrchestratorService orchestratorService;

    public ExecutionController(OrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @PostMapping("/tasks/{taskId}/executions")
    public ResponseEntity<ExecutionResponse> triggerExecution(
            @PathVariable String taskId,
            @Valid @RequestBody ExecutionCreateInput input) {
        Execution created = orchestratorService.triggerExecution(
                taskId,
                input.getAgentId(),
                input.getCommandLine()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ExecutionResponse.fromDomain(created));
    }

    @GetMapping("/tasks/{taskId}/executions")
    public ResponseEntity<List<ExecutionResponse>> listExecutions(@PathVariable String taskId) {
        List<ExecutionResponse> responses = orchestratorService.listExecutionsByTask(taskId)
                .stream()
                .map(ExecutionResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/executions/{id}")
    public ResponseEntity<ExecutionResponse> getExecutionById(@PathVariable String id) {
        Execution execution = orchestratorService.getExecutionById(id);
        return ResponseEntity.ok(ExecutionResponse.fromDomain(execution));
    }

    @PostMapping("/executions/{id}/approve")
    public ResponseEntity<ExecutionResponse> approveExecution(@PathVariable String id) {
        Execution approved = orchestratorService.approveExecution(id);
        return ResponseEntity.ok(ExecutionResponse.fromDomain(approved));
    }

    @PostMapping("/executions/{id}/reject")
    public ResponseEntity<ExecutionResponse> rejectExecution(@PathVariable String id) {
        Execution rejected = orchestratorService.rejectExecution(id);
        return ResponseEntity.ok(ExecutionResponse.fromDomain(rejected));
    }

    @GetMapping("/executions/{id}/logs")
    public ResponseEntity<String> getExecutionLogs(@PathVariable String id) {
        String logContent = orchestratorService.getLogContent(id);
        return ResponseEntity.ok(logContent);
    }
}
