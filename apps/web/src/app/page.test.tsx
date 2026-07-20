import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import Home from './page';
import { api } from '../services/api';

vi.mock('../services/api', () => ({
  api: {
    listProjects: vi.fn(),
    createProject: vi.fn(),
    archiveProject: vi.fn(),
    listAgents: vi.fn().mockResolvedValue([]),
    createAgent: vi.fn(),
    listTasks: vi.fn().mockResolvedValue([]),
    createTask: vi.fn(),
    changeTaskStatus: vi.fn(),
    listExecutions: vi.fn().mockResolvedValue([]),
    getExecutionById: vi.fn(),
    triggerExecution: vi.fn(),
    approveExecution: vi.fn(),
    rejectExecution: vi.fn(),
    getExecutionLogs: vi.fn().mockResolvedValue("")
  }
}));

describe('Home Page Dashboard', () => {
  it('renders title and sub-heading initially', async () => {
    vi.mocked(api.listProjects).mockReturnValue(new Promise(() => {}));

    render(<Home />);

    expect(screen.getByText('Orquestador de Agentes IA')).toBeDefined();
    expect(screen.getByText('Control de Agentes Autónomos Multiproyecto')).toBeDefined();
  });

  it('renders empty state when no projects returned', async () => {
    vi.mocked(api.listProjects).mockResolvedValue([]);

    render(<Home />);

    await waitFor(() => {
      expect(screen.getByText('No hay proyectos registrados en el orquestador.')).toBeDefined();
    });
  });

  it('renders projects list when projects exist', async () => {
    vi.mocked(api.listProjects).mockResolvedValue([
      {
        id: '1',
        key: 'PROJECT-1',
        name: 'Project One',
        description: 'First project desc',
        status: 'ACTIVE',
        repositoryPath: '/tmp/repo-1',
        defaultBranch: 'main',
        contextPath: '.ai',
        createdAt: '2026-07-14T21:40:00Z',
        updatedAt: '2026-07-14T21:45:00Z'
      }
    ]);

    render(<Home />);

    await waitFor(() => {
      expect(screen.getByText('Project One')).toBeDefined();
      expect(screen.getByText('First project desc')).toBeDefined();
      expect(screen.getByText('ACTIVE')).toBeDefined();
    });
  });

  it('renders task role badges, Gemini AI Run mode and contract tabs', async () => {
    vi.mocked(api.listProjects).mockResolvedValue([
      {
        id: 'p-1',
        key: 'ORQ',
        name: 'Orchestrator Project',
        description: 'Test project',
        status: 'ACTIVE',
        repositoryPath: '/tmp/repo',
        defaultBranch: 'main',
        contextPath: '.ai',
        createdAt: '2026-07-14T21:40:00Z',
        updatedAt: '2026-07-14T21:45:00Z'
      }
    ]);

    vi.mocked(api.listTasks).mockResolvedValue([
      {
        id: 'task-101',
        projectId: 'p-1',
        title: 'Task UI Integration Test',
        description: 'Testing task UI tabs',
        status: 'IN_PROGRESS',
        assigneeId: 'agent-dev',
        reviewerId: 'agent-rev',
        createdAt: '2026-07-14T21:40:00Z',
        updatedAt: '2026-07-14T21:45:00Z'
      }
    ]);

    vi.mocked(api.listAgents).mockResolvedValue([
      { id: 'agent-dev', name: 'Dev Agent', role: '@developer', modelName: 'gemini-1.5-flash', temperature: 0.2 },
      { id: 'agent-rev', name: 'Reviewer Agent', role: '@reviewer', modelName: 'gemini-1.5-pro', temperature: 0.1 }
    ]);

    render(<Home />);

    await waitFor(() => {
      expect(screen.getByText('Orchestrator Project')).toBeDefined();
    });

    fireEvent.click(screen.getByText('Orchestrator Project'));

    await waitFor(() => {
      expect(screen.getByText('Task UI Integration Test')).toBeDefined();
    });

    fireEvent.click(screen.getByText('Task UI Integration Test'));

    await waitFor(() => {
      expect(screen.getByText('Gemini IA Run')).toBeDefined();
      expect(screen.getByText('Contrato TASK_CONTRACT.md')).toBeDefined();
      expect(screen.getByText('Evidencia EVIDENCE_LOG.md')).toBeDefined();
    });
  });
});
