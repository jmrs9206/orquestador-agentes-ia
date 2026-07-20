package com.orquestador.orchestrator.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orquestador.orchestrator.api.dtos.AgentCreateInput;
import com.orquestador.orchestrator.api.dtos.ExecutionCreateInput;
import com.orquestador.orchestrator.api.dtos.TaskCreateInput;
import com.orquestador.orchestrator.api.dtos.TaskStatusInput;
import com.orquestador.orchestrator.application.OrchestratorService;
import com.orquestador.orchestrator.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({AgentController.class, TaskController.class, ExecutionController.class})
class OrchestratorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrchestratorService orchestratorService;

    @Test
    void shouldRegisterAgent() throws Exception {
        Agent agent = new Agent("agent-1", "Bob", "DEVELOPER", "Prompt", "gemini-flash", 0.7);
        when(orchestratorService.createAgent(anyString(), anyString(), anyString(), anyString(), anyDouble()))
                .thenReturn(agent);

        AgentCreateInput input = new AgentCreateInput();
        input.setName("Bob");
        input.setRole("DEVELOPER");
        input.setSystemPrompt("Prompt");
        input.setModelName("gemini-flash");
        input.setTemperature(0.7);

        mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.role").value("DEVELOPER"));
    }

    @Test
    void shouldCreateTask() throws Exception {
        Task task = new Task("task-1", "proj-1", "Task Title", "Desc", TaskStatus.DRAFT, "agent-1", "agent-2", LocalDateTime.now(), LocalDateTime.now());
        when(orchestratorService.createTask(eq("proj-1"), eq("Task Title"), eq("Desc"), eq("agent-1"), eq("agent-2")))
                .thenReturn(task);

        TaskCreateInput input = new TaskCreateInput();
        input.setTitle("Task Title");
        input.setDescription("Desc");
        input.setAssigneeId("agent-1");
        input.setReviewerId("agent-2");

        mockMvc.perform(post("/api/projects/proj-1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("task-1"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void shouldFailCrossReviewOnTaskClose() throws Exception {
        when(orchestratorService.changeTaskStatus(eq("task-1"), eq(TaskStatus.DONE)))
                .thenThrow(new IllegalArgumentException("La validación cruzada ha fallado"));

        TaskStatusInput input = new TaskStatusInput();
        input.setStatus(TaskStatus.DONE);

        mockMvc.perform(patch("/api/tasks/task-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST_PARAMETER"))
                .andExpect(jsonPath("$.message").value("La validación cruzada ha fallado"));
    }

    @Test
    void shouldTriggerExecution() throws Exception {
        Execution execution = new Execution("exec-1", "task-1", "agent-1", ExecutionStatus.PENDING, "echo 'hello'", ".ai/logs/exec-1.log", null, LocalDateTime.now(), null);
        when(orchestratorService.triggerExecution(eq("task-1"), eq("agent-1"), eq("echo 'hello'")))
                .thenReturn(execution);

        ExecutionCreateInput input = new ExecutionCreateInput();
        input.setAgentId("agent-1");
        input.setCommandLine("echo 'hello'");

        mockMvc.perform(post("/api/tasks/task-1/executions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.commandLine").value("echo 'hello'"));
    }

    @Test
    void shouldApproveExecution() throws Exception {
        Execution execution = new Execution("exec-1", "task-1", "agent-1", ExecutionStatus.PENDING, "git push", ".ai/logs/exec-1.log", null, LocalDateTime.now(), null);
        when(orchestratorService.approveExecution(eq("exec-1"))).thenReturn(execution);

        mockMvc.perform(post("/api/executions/exec-1/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldRejectExecution() throws Exception {
        Execution execution = new Execution("exec-1", "task-1", "agent-1", ExecutionStatus.FAILED, "git push", ".ai/logs/exec-1.log", -2, LocalDateTime.now(), LocalDateTime.now());
        when(orchestratorService.rejectExecution(eq("exec-1"))).thenReturn(execution);

        mockMvc.perform(post("/api/executions/exec-1/reject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.exitCode").value(-2));
    }
}
