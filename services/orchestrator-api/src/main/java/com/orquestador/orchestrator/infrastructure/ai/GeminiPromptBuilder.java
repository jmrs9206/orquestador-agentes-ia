package com.orquestador.orchestrator.infrastructure.ai;

import com.orquestador.orchestrator.domain.Agent;
import com.orquestador.orchestrator.domain.Project;
import com.orquestador.orchestrator.domain.Task;
import com.orquestador.orchestrator.infrastructure.ai.dto.GeminiRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GeminiPromptBuilder {

    public GeminiRequest buildRequest(Agent agent, Project project, Task task, String projectContextContent) {
        String systemInstructionText = buildSystemInstruction(agent);
        String userPromptText = buildUserPrompt(project, task, projectContextContent);

        GeminiRequest.SystemInstruction systemInstruction = new GeminiRequest.SystemInstruction(systemInstructionText);
        GeminiRequest.Content userContent = new GeminiRequest.Content("user", userPromptText);
        GeminiRequest.GenerationConfig generationConfig = new GeminiRequest.GenerationConfig(agent.getTemperature());

        return new GeminiRequest(
                systemInstruction,
                Collections.singletonList(userContent),
                generationConfig
        );
    }

    public String buildSystemInstruction(Agent agent) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are acting as an AI Agent in an Orchestrated Multi-Agent Architecture.\n");
        if (agent.getRole() != null && !agent.getRole().isBlank()) {
            sb.append("Assigned Role: ").append(agent.getRole()).append("\n");
        }
        if (agent.getName() != null && !agent.getName().isBlank()) {
            sb.append("Agent Name: ").append(agent.getName()).append("\n");
        }
        sb.append("\n=== SYSTEM INSTRUCTIONS & BEHAVIOR ===\n");
        if (agent.getSystemPrompt() != null && !agent.getSystemPrompt().isBlank()) {
            sb.append(agent.getSystemPrompt()).append("\n");
        } else {
            sb.append("Perform the requested task according to the project rules and acceptance criteria.\n");
        }
        return sb.toString();
    }

    public String buildUserPrompt(Project project, Task task, String projectContextContent) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== PROJECT INFORMATION ===\n");
        if (project != null) {
            sb.append("Project ID: ").append(project.getId()).append("\n");
            sb.append("Project Key: ").append(project.getKey()).append("\n");
            sb.append("Project Name: ").append(project.getName()).append("\n");
            sb.append("Repository Path: ").append(project.getRepositoryPath()).append("\n");
            sb.append("Default Branch: ").append(project.getDefaultBranch()).append("\n");
        }
        sb.append("\n=== PROJECT CONTEXT & GOVERNANCE RULES ===\n");
        if (projectContextContent != null && !projectContextContent.isBlank()) {
            sb.append(projectContextContent).append("\n");
        } else {
            sb.append("No specific context provided. Follow strict project isolation and verification rules.\n");
        }

        sb.append("\n=== ASSIGNED TASK DETAILS ===\n");
        if (task != null) {
            sb.append("Task ID: ").append(task.getId()).append("\n");
            sb.append("Title: ").append(task.getTitle()).append("\n");
            sb.append("Description: ").append(task.getDescription() != null ? task.getDescription() : "No description provided.").append("\n");
            sb.append("Status: ").append(task.getStatus()).append("\n");
            sb.append("Reviewer ID: ").append(task.getReviewerId() != null ? task.getReviewerId() : "Unassigned").append("\n");
        }

        sb.append("\n=== OUTPUT INSTRUCTIONS ===\n");
        sb.append("1. Provide your exact technical execution plan or solution for this task.\n");
        sb.append("2. Include all necessary file changes, commands, and verification steps.\n");
        sb.append("3. Distinguish clearly between verified facts, inferences, proposals, and unknowns.\n");
        return sb.toString();
    }
}
