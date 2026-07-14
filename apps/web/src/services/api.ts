export interface Project {
  id: string;
  key: string;
  name: string;
  description?: string;
  status: 'DRAFT' | 'ACTIVE' | 'PAUSED' | 'BLOCKED' | 'ARCHIVED';
  repositoryPath: string;
  defaultBranch: string;
  contextPath: string;
  createdAt: string;
  updatedAt: string;
  archivedAt?: string;
}

export interface ProjectCreateInput {
  key: string;
  name: string;
  description?: string;
  repositoryPath: string;
  defaultBranch?: string;
  contextPath?: string;
}

export interface ProjectUpdateInput {
  name?: string;
  description?: string;
  defaultBranch?: string;
}

export interface ErrorResponse {
  code: string;
  message: string;
  timestamp: string;
  details?: string[];
}

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    let errorData: ErrorResponse;
    try {
      errorData = await response.json();
    } catch {
      errorData = {
        code: 'UNKNOWN_ERROR',
        message: 'Ocurrió un error inesperado al comunicarse con el servidor.',
        timestamp: new Date().toISOString()
      };
    }
    throw errorData;
  }
  return response.json() as Promise<T>;
}

export const api = {
  async listProjects(includeArchived: boolean = false): Promise<Project[]> {
    const url = `${API_BASE_URL}/projects?includeArchived=${includeArchived}`;
    const res = await fetch(url, { cache: 'no-store' });
    return handleResponse<Project[]>(res);
  },

  async getProjectById(id: string): Promise<Project> {
    const url = `${API_BASE_URL}/projects/${id}`;
    const res = await fetch(url, { cache: 'no-store' });
    return handleResponse<Project>(res);
  },

  async createProject(input: ProjectCreateInput): Promise<Project> {
    const url = `${API_BASE_URL}/projects`;
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(input)
    });
    return handleResponse<Project>(res);
  },

  async updateProject(id: string, input: ProjectUpdateInput): Promise<Project> {
    const url = `${API_BASE_URL}/projects/${id}`;
    const res = await fetch(url, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(input)
    });
    return handleResponse<Project>(res);
  },

  async archiveProject(id: string): Promise<Project> {
    const url = `${API_BASE_URL}/projects/${id}/archive`;
    const res = await fetch(url, {
      method: 'PUT'
    });
    return handleResponse<Project>(res);
  }
};
