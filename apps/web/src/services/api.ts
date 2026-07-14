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

export interface Agent {
  id: string;
  name: string;
  role: string;
  systemPrompt?: string;
  modelName: string;
  temperature: number;
}

export interface AgentCreateInput {
  name: string;
  role: string;
  systemPrompt?: string;
  modelName: string;
  temperature: number;
}

export type TaskStatus = 'DRAFT' | 'READY' | 'IN_PROGRESS' | 'NEEDS_REVIEW' | 'DONE';

export interface Task {
  id: string;
  projectId: string;
  title: string;
  description?: string;
  status: TaskStatus;
  assigneeId?: string;
  reviewerId?: string;
  createdAt: string;
  updatedAt: string;
}

export interface TaskCreateInput {
  title: string;
  description?: string;
  assigneeId?: string;
  reviewerId?: string;
}

export type ExecutionStatus = 'PENDING' | 'RUNNING' | 'SUCCESS' | 'FAILED' | 'WAITING_APPROVAL';

export interface Execution {
  id: string;
  taskId: string;
  agentId: string;
  status: ExecutionStatus;
  commandLine: string;
  logFilePath: string;
  exitCode?: number;
  startedAt: string;
  finishedAt?: string;
}

export interface ExecutionCreateInput {
  agentId: string;
  commandLine: string;
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
  // Project operations
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
  },

  // Agent operations
  async listAgents(): Promise<Agent[]> {
    const url = `${API_BASE_URL}/agents`;
    const res = await fetch(url, { cache: 'no-store' });
    return handleResponse<Agent[]>(res);
  },

  async createAgent(input: AgentCreateInput): Promise<Agent> {
    const url = `${API_BASE_URL}/agents`;
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(input)
    });
    return handleResponse<Agent>(res);
  },

  // Task operations
  async listTasks(projectId: string): Promise<Task[]> {
    const url = `${API_BASE_URL}/projects/${projectId}/tasks`;
    const res = await fetch(url, { cache: 'no-store' });
    return handleResponse<Task[]>(res);
  },

  async createTask(projectId: string, input: TaskCreateInput): Promise<Task> {
    const url = `${API_BASE_URL}/projects/${projectId}/tasks`;
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(input)
    });
    return handleResponse<Task>(res);
  },

  async changeTaskStatus(taskId: string, status: TaskStatus): Promise<Task> {
    const url = `${API_BASE_URL}/tasks/${taskId}/status`;
    const res = await fetch(url, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ status })
    });
    return handleResponse<Task>(res);
  },

  // Execution operations
  async listExecutions(taskId: string): Promise<Execution[]> {
    const url = `${API_BASE_URL}/tasks/${taskId}/executions`;
    const res = await fetch(url, { cache: 'no-store' });
    return handleResponse<Execution[]>(res);
  },

  async getExecutionById(id: string): Promise<Execution> {
    const url = `${API_BASE_URL}/executions/${id}`;
    const res = await fetch(url, { cache: 'no-store' });
    return handleResponse<Execution>(res);
  },

  async triggerExecution(taskId: string, input: ExecutionCreateInput): Promise<Execution> {
    const url = `${API_BASE_URL}/tasks/${taskId}/executions`;
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(input)
    });
    return handleResponse<Execution>(res);
  },

  async approveExecution(id: string): Promise<Execution> {
    const url = `${API_BASE_URL}/executions/${id}/approve`;
    const res = await fetch(url, {
      method: 'POST'
    });
    return handleResponse<Execution>(res);
  },

  async rejectExecution(id: string): Promise<Execution> {
    const url = `${API_BASE_URL}/executions/${id}/reject`;
    const res = await fetch(url, {
      method: 'POST'
    });
    return handleResponse<Execution>(res);
  },

  async getExecutionLogs(id: string): Promise<string> {
    const url = `${API_BASE_URL}/executions/${id}/logs`;
    const res = await fetch(url, { cache: 'no-store' });
    if (!res.ok) {
      let msg = 'Error al obtener logs';
      try {
        const errorData = await res.json();
        msg = errorData.message || msg;
      } catch {
        // use default
      }
      throw { message: msg } as ErrorResponse;
    }
    return res.text();
  }
};
