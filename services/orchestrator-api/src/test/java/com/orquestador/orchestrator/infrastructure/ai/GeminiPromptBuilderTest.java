package com.orquestador.orchestrator.infrastructure.ai;

import com.orquestador.orchestrator.domain.Agent;
import com.orquestador.orchestrator.domain.Project;
import com.orquestador.orchestrator.domain.ProjectStatus;
import com.orquestador.orchestrator.domain.Task;
import com.orquestador.orchestrator.domain.TaskStatus;
import com.orquestador.orchestrator.infrastructure.ai.dto.GeminiRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GeminiPromptBuilderTest {

    private GeminiPromptBuilder promptBuilder;

    @BeforeEach
    void setUp() {
        promptBuilder = new GeminiPromptBuilder();
    }

    @Test
    void shouldBuildValidGeminiRequestFromDomainEntities() {
        Agent agent = new Agent("agent-1", "Backend Developer Agent", "@backend", "You construct Spring Boot APIs safely.", "gemini-1.5-flash", 0.2);
        Project project = new Project("p-1", "orq", "Orquestador IA", "Sistema de agentes", ProjectStatus.ACTIVE, "/repo/path", "main", ".ai/PROJECT_CONTEXT.md", LocalDateTime.now(), LocalDateTime.now(), null);
        Task task = new Task("task-011", "p-1", "Gemini API Integration", "Integrar cliente Gemini API", TaskStatus.IN_PROGRESS, "agent-1", "reviewer-1", LocalDateTime.now(), LocalDateTime.now());
        String projectContext = "Rules: Do not invent requirements or assume unverified state.";

        GeminiRequest request = promptBuilder.buildRequest(agent, project, task, projectContext);

        assertNotNull(request);
        assertNotNull(request.getSystemInstruction());
        assertEquals(1, request.getSystemInstruction().getParts().size());
        assertTrue(request.getSystemInstruction().getParts().get(0).getText().contains("Assigned Role: @backend"));
        assertTrue(request.getSystemInstruction().getParts().get(0).getText().contains("You construct Spring Boot APIs safely."));

        assertNotNull(request.getContents());
        assertEquals(1, request.getContents().size());
        GeminiRequest.Content content = request.getContents().get(0);
        assertEquals("user", content.getRole());
        String userText = content.getParts().get(0).getText();
        assertTrue(userText.contains("Project ID: p-1"));
        assertTrue(userText.contains("Task ID: task-011"));
        assertTrue(userText.contains("Rules: Do not invent requirements"));

        assertNotNull(request.getGenerationConfig());
        assertEquals(0.2, request.getGenerationConfig().getTemperature());
    }

    @Test
    void shouldHandleNullOrEmptyValuesGracefully() {
        Agent agent = new Agent("agent-2", null, null, null, null, 0.7);

        GeminiRequest request = promptBuilder.buildRequest(agent, null, null, null);

        assertNotNull(request);
        assertNotNull(request.getSystemInstruction());
        assertTrue(request.getSystemInstruction().getParts().get(0).getText().contains("Perform the requested task according to the project rules"));

        String userText = request.getContents().get(0).getParts().get(0).getText();
        assertTrue(userText.contains("No specific context provided."));
    }
}
