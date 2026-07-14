# Task Contract - TASK_016: E2E Playwright Tests for AI Agent Loop

- Iteration ID: `it-03`
- Task ID: `task-016`
- Title: Pruebas Integrales de Agentes IA y Sandbox
- Status: `DRAFT`
- Assigned role: `@developer`
- Reviewer role: `@reviewer`
- Run ID: `none`
- Branch/worktree: `feature/ai-integration-layer`

## Objective
Implement E2E test flows in Playwright verifying prompt submissions, sandboxed filesystem violations blocking, contract generations, and auditor validation rules.

## Acceptance Criteria
1. **AI Flow Execution E2E test**: Simulate selecting an agent, writing a task prompt, executing it via Gemini, and verifying that logs/evidence are successfully retrieved.
2. **Directory Sandbox Violation E2E test**: Trigger a command designed to read `/etc/passwd` or use `../../` traversal paths, and assert that the UI displays a warning banner and the execution is blocked with `SecurityException` logs.
3. **Audit File Generation Assertions**: Verify that the workspace console displays the generated `TASK_CONTRACT.md` and `EVIDENCE_LOG.md` properly.
4. **E2E execution success**: Run the Playwright test suite and assert 100% green passage locally.

## Verification Method
- Execute npx playwright test --project=chromium verifying AI sandbox E2E flows.
