# Task Contract - TASK_011: Gemini API Backend Integration

- Iteration ID: `it-03`
- Task ID: `task-011`
- Title: Integración del Cliente Gemini API
- Status: `DRAFT`
- Assigned role: `@developer`
- Reviewer role: `@reviewer`
- Run ID: `none`
- Branch/worktree: `feature/ai-integration-layer`

## Objective
Configure and implement the Gemini API Client integration inside the Spring Boot backend to support actual AI-based agent executions.

## Acceptance Criteria
1. **Gemini SDK/HTTP client integration**: Set up the Spring Boot client (using official Google Gen AI Java SDK or direct `WebClient`/`RestTemplate` endpoints).
2. **Dynamic Authentication**: Consume `GEMINI_API_KEY` environment variable dynamically injected from the environment.
3. **Structured Prompt Construction**: Implement a builder that compiles:
   - System Prompt of the assigned agent.
   - Project Context and default rules.
   - Task Details (inputs, description).
4. **Structured JSON/Markdown parsing**: Capture and parse response payload safely, updating the Execution logs.
5. **Unit Testing**: Write Mockito/Spring Boot tests verifying prompt construction and simulated API response parsing.

## Verification Method
- Execute Maven test suite verifying API client response mock parsing.
