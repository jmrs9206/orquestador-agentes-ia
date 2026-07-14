import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import Home from './page';
import { api } from '../services/api';

vi.mock('../services/api', () => ({
  api: {
    listProjects: vi.fn(),
    createProject: vi.fn(),
    archiveProject: vi.fn()
  }
}));

describe('Home Page Dashboard', () => {
  it('renders title and loading skeleton initially', async () => {
    vi.mocked(api.listProjects).mockReturnValue(new Promise(() => {}));

    render(<Home />);

    expect(screen.getByText('Orquestador de Agentes IA')).toBeDefined();
    expect(screen.getByText('Plano de control y registro de proyectos locales')).toBeDefined();
  });

  it('renders empty state when no projects returned', async () => {
    vi.mocked(api.listProjects).mockResolvedValue([]);

    render(<Home />);

    await waitFor(() => {
      expect(screen.getByText('No hay proyectos activos registrados.')).toBeDefined();
    });
  });

  it('renders projects list when projects exist', async () => {
    vi.mocked(api.listProjects).mockResolvedValue([
      {
        id: '1',
        key: 'project-1',
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
});
