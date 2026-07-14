package com.orquestador.orchestrator.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orquestador.orchestrator.api.dtos.ProjectCreateInput;
import com.orquestador.orchestrator.api.dtos.ProjectUpdateInput;
import com.orquestador.orchestrator.application.ProjectService;
import com.orquestador.orchestrator.domain.Project;
import com.orquestador.orchestrator.domain.ProjectStatus;
import com.orquestador.orchestrator.domain.exceptions.DuplicateProjectKeyException;
import com.orquestador.orchestrator.domain.exceptions.InvalidProjectStateException;
import com.orquestador.orchestrator.domain.exceptions.InvalidRepositoryPathException;
import com.orquestador.orchestrator.domain.exceptions.ProjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    @Test
    void shouldRegisterProjectSuccessfully() throws Exception {
        Project mockProject = new Project(
                "uuid-123", "my-key", "My Project", "Description", ProjectStatus.DRAFT,
                "/tmp/my-repo", "main", ".ai", LocalDateTime.now(), LocalDateTime.now(), null
        );

        when(projectService.createProject(
                eq("my-key"), eq("My Project"), eq("Description"), eq("/tmp/my-repo"), eq("main"), eq(".ai")
        )).thenReturn(mockProject);

        ProjectCreateInput input = new ProjectCreateInput();
        input.setKey("my-key");
        input.setName("My Project");
        input.setDescription("Description");
        input.setRepositoryPath("/tmp/my-repo");
        input.setDefaultBranch("main");
        input.setContextPath(".ai");

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("uuid-123"))
                .andExpect(jsonPath("$.key").value("my-key"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void shouldReturnBadRequestWhenKeyIsInvalid() throws Exception {
        ProjectCreateInput input = new ProjectCreateInput();
        input.setKey("INVALID KEY WITH SPACES");
        input.setName("My Project");
        input.setRepositoryPath("/tmp/my-repo");

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.details[0]").value("key: La clave (key) debe ser alfanumérica en minúsculas, guiones o guiones bajos"));
    }

    @Test
    void shouldReturnConflictWhenKeyIsDuplicate() throws Exception {
        when(projectService.createProject(
                any(), any(), any(), any(), any(), any()
        )).thenThrow(new DuplicateProjectKeyException("Ya existe un proyecto con esa clave"));

        ProjectCreateInput input = new ProjectCreateInput();
        input.setKey("duplicate-key");
        input.setName("My Project");
        input.setRepositoryPath("/tmp/my-repo");

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_PROJECT_KEY"))
                .andExpect(jsonPath("$.message").value("Ya existe un proyecto con esa clave"));
    }

    @Test
    void shouldReturnBadRequestWhenRepositoryPathDoesNotExist() throws Exception {
        when(projectService.createProject(
                any(), any(), any(), any(), any(), any()
        )).thenThrow(new InvalidRepositoryPathException("La ruta especificada no existe"));

        ProjectCreateInput input = new ProjectCreateInput();
        input.setKey("some-key");
        input.setName("My Project");
        input.setRepositoryPath("/non-existent/path");

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REPOSITORY_PATH"))
                .andExpect(jsonPath("$.message").value("La ruta especificada no existe"));
    }

    @Test
    void shouldListProjects() throws Exception {
        Project mockProject = new Project(
                "uuid-123", "key-1", "Project 1", null, ProjectStatus.ACTIVE,
                "/tmp/repo-1", "main", ".ai", LocalDateTime.now(), LocalDateTime.now(), null
        );

        when(projectService.listProjects(false)).thenReturn(List.of(mockProject));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key").value("key-1"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    void shouldGetProjectById() throws Exception {
        Project mockProject = new Project(
                "uuid-123", "key-1", "Project 1", null, ProjectStatus.ACTIVE,
                "/tmp/repo-1", "main", ".ai", LocalDateTime.now(), LocalDateTime.now(), null
        );

        when(projectService.getProjectById("uuid-123")).thenReturn(mockProject);

        mockMvc.perform(get("/api/projects/uuid-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("key-1"));
    }

    @Test
    void shouldReturnNotFoundWhenProjectDoesNotExist() throws Exception {
        when(projectService.getProjectById("non-existent")).thenThrow(new ProjectNotFoundException("No existe"));

        mockMvc.perform(get("/api/projects/non-existent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROJECT_NOT_FOUND"));
    }

    @Test
    void shouldUpdateProject() throws Exception {
        Project mockProject = new Project(
                "uuid-123", "key-1", "Project Updated", "New Desc", ProjectStatus.ACTIVE,
                "/tmp/repo-1", "master", ".ai", LocalDateTime.now(), LocalDateTime.now(), null
        );

        when(projectService.updateProject(eq("uuid-123"), eq("Project Updated"), eq("New Desc"), eq("master")))
                .thenReturn(mockProject);

        ProjectUpdateInput input = new ProjectUpdateInput();
        input.setName("Project Updated");
        input.setDescription("New Desc");
        input.setDefaultBranch("master");

        mockMvc.perform(patch("/api/projects/uuid-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Project Updated"))
                .andExpect(jsonPath("$.description").value("New Desc"))
                .andExpect(jsonPath("$.defaultBranch").value("master"));
    }

    @Test
    void shouldArchiveProject() throws Exception {
        Project mockProject = new Project(
                "uuid-123", "key-1", "Project 1", null, ProjectStatus.ARCHIVED,
                "/tmp/repo-1", "main", ".ai", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()
        );

        when(projectService.archiveProject("uuid-123")).thenReturn(mockProject);

        mockMvc.perform(put("/api/projects/uuid-123/archive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARCHIVED"))
                .andExpect(jsonPath("$.archivedAt").isNotEmpty());
    }

    @Test
    void shouldReturnBadRequestWhenArchivingAlreadyArchivedProject() throws Exception {
        when(projectService.archiveProject("uuid-123")).thenThrow(new InvalidProjectStateException("Ya archivado"));

        mockMvc.perform(put("/api/projects/uuid-123/archive"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PROJECT_STATE"))
                .andExpect(jsonPath("$.message").value("Ya archivado"));
    }
}
