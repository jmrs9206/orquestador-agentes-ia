# Task Contract - TASK_015: Frontend Agent Workspace Integration

- Iteration ID: `it-03`
- Task ID: `task-015`
- Title: Integración UI del Workspace de Agentes IA
- Status: `DONE`
- Assigned role: `@frontend`
- Reviewer role: `@reviewer`
- Run ID: `bootstrap-github-2026-07-14`
- Branch/worktree: `feature/ai-integration-layer`

## Objective
Extend the Next.js project workspace to display task contracts, view generated evidences, and trigger automated AI executions using the Gemini model panel.

## Acceptance Criteria
1. **Gemini Run Trigger Panel**: Add an interactive interface allowing the user to select an agent, input a prompt or target, and launch the Gemini run loop.
2. **Contract & Evidence viewer**: Add tabs/sections in the workspace console displaying `TASK_CONTRACT.md` and `EVIDENCE_LOG.md` formatted beautifully from the filesystem.
3. **Role Badge Identifiers**: Render color-coded role tags (e.g. `@reviewer`, `@developer`) on task details.
4. **Unit testing**: Vitest assertions validating form submissions and panels rendering.

## Verification Method
- Run npm run test in web package verifying UI components mock-rendering.
