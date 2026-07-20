package com.orquestador.orchestrator.api;

import com.orquestador.orchestrator.api.dtos.TaskCreateInput;
import com.orquestador.orchestrator.api.dtos.TaskResponse;
import com.orquestador.orchestrator.api.dtos.TaskStatusInput;
import com.orquestador.orchestrator.application.OrchestratorService;
import com.orquestador.orchestrator.domain.Task;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TaskController {

    private final OrchestratorService orchestratorService;

    public TaskController(OrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable String projectId,
            @Valid @RequestBody TaskCreateInput input) {
        Task created = orchestratorService.createTask(
                projectId,
                input.getTitle(),
                input.getDescription(),
                input.getAssigneeId(),
                input.getReviewerId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskResponse.fromDomain(created));
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskResponse>> listTasks(@PathVariable String projectId) {
        List<TaskResponse> responses = orchestratorService.listTasksByProject(projectId)
                .stream()
                .map(TaskResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/tasks/{id}/status")
    public ResponseEntity<TaskResponse> changeTaskStatus(
            @PathVariable String id,
            @Valid @RequestBody TaskStatusInput input) {
        Task updated = orchestratorService.changeTaskStatus(id, input.getStatus());
        return ResponseEntity.ok(TaskResponse.fromDomain(updated));
    }
}
