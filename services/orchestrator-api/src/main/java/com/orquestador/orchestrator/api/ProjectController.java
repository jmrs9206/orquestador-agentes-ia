package com.orquestador.orchestrator.api;

import com.orquestador.orchestrator.api.dtos.ProjectCreateInput;
import com.orquestador.orchestrator.api.dtos.ProjectResponse;
import com.orquestador.orchestrator.api.dtos.ProjectUpdateInput;
import com.orquestador.orchestrator.application.ProjectService;
import com.orquestador.orchestrator.domain.Project;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> registerProject(@Valid @RequestBody ProjectCreateInput input) {
        Project created = projectService.createProject(
                input.getKey(),
                input.getName(),
                input.getDescription(),
                input.getRepositoryPath(),
                input.getDefaultBranch(),
                input.getContextPath()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ProjectResponse.fromDomain(created));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> listProjects(
            @RequestParam(name = "includeArchived", required = false, defaultValue = "false") boolean includeArchived) {
        List<ProjectResponse> responses = projectService.listProjects(includeArchived)
                .stream()
                .map(ProjectResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable String id) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(ProjectResponse.fromDomain(project));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable String id,
            @Valid @RequestBody ProjectUpdateInput input) {
        Project updated = projectService.updateProject(
                id,
                input.getName(),
                input.getDescription(),
                input.getDefaultBranch()
        );
        return ResponseEntity.ok(ProjectResponse.fromDomain(updated));
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<ProjectResponse> archiveProject(@PathVariable String id) {
        Project archived = projectService.archiveProject(id);
        return ResponseEntity.ok(ProjectResponse.fromDomain(archived));
    }
}
