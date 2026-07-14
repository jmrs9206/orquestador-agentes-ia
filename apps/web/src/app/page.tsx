"use client";

import React, { useState, useEffect, useCallback, useRef } from "react";
import { api, Project, Agent, Task, Execution, TaskStatus, ErrorResponse } from "../services/api";

export default function Home() {
  // Main Navigation / Selection State
  const [projects, setProjects] = useState<Project[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedProject, setSelectedProject] = useState<Project | null>(null);

  // Form State for Project Registration
  const [key, setKey] = useState("");
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [repositoryPath, setRepositoryPath] = useState("");
  const [defaultBranch, setDefaultBranch] = useState("main");
  const [contextPath, setContextPath] = useState(".ai");

  // Project Form Feedback
  const [formLoading, setFormLoading] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);
  const [formSuccess, setFormSuccess] = useState(false);

  // Project Workspace Details State
  const [tasks, setTasks] = useState<Task[]>([]);
  const [agents, setAgents] = useState<Agent[]>([]);
  const [selectedTask, setSelectedTask] = useState<Task | null>(null);
  const [executions, setExecutions] = useState<Execution[]>([]);
  const [selectedExecution, setSelectedExecution] = useState<Execution | null>(null);
  const [executionLogs, setExecutionLogs] = useState<string>("");
  const [activeTab, setActiveTab] = useState<"tasks" | "agents">("tasks");

  // Form State for Agent Creation
  const [agentName, setAgentName] = useState("");
  const [agentRole, setAgentRole] = useState("DEVELOPER");
  const [agentPrompt, setAgentPrompt] = useState("");
  const [agentModel, setAgentModel] = useState("gemini-1.5-flash");
  const [agentTemp, setAgentTemp] = useState<number>(0.7);
  const [agentFormError, setAgentFormError] = useState<string | null>(null);

  // Form State for Task Creation
  const [taskTitle, setTaskTitle] = useState("");
  const [taskDesc, setTaskDesc] = useState("");
  const [taskAssignee, setTaskAssignee] = useState("");
  const [taskReviewer, setTaskReviewer] = useState("");
  const [taskFormError, setTaskFormError] = useState<string | null>(null);

  // Form State for Execution Trigger
  const [execAgentId, setExecAgentId] = useState("");
  const [execCmdLine, setExecCmdLine] = useState("");
  const [execFormError, setExecFormError] = useState<string | null>(null);

  // Console terminal automatic scrolling ref
  const terminalEndRef = useRef<HTMLDivElement | null>(null);

  // 1. Fetching logic
  const fetchProjects = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await api.listProjects(false);
      setProjects(data);
    } catch (err: unknown) {
      const errorResp = err as ErrorResponse;
      setError(errorResp.message || "No se pudieron cargar los proyectos.");
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchWorkspaceData = useCallback(async (projectId: string) => {
    try {
      const [fetchedTasks, fetchedAgents] = await Promise.all([
        api.listTasks(projectId),
        api.listAgents()
      ]);
      setTasks(fetchedTasks);
      setAgents(fetchedAgents);

      // Keep selected task updated
      if (selectedTask) {
        const refreshedTask = fetchedTasks.find(t => t.id === selectedTask.id);
        if (refreshedTask) {
          setSelectedTask(refreshedTask);
        }
      }
    } catch (err: unknown) {
      console.error("Error fetching workspace data:", err);
    }
  }, [selectedTask]);

  const fetchExecutions = useCallback(async (taskId: string) => {
    try {
      const data = await api.listExecutions(taskId);
      setExecutions(data);

      if (selectedExecution) {
        const refreshedExec = data.find(e => e.id === selectedExecution.id);
        if (refreshedExec) {
          setSelectedExecution(refreshedExec);
        }
      }
    } catch (err: unknown) {
      console.error("Error fetching executions:", err);
    }
  }, [selectedExecution]);

  const fetchLogs = useCallback(async (execId: string) => {
    try {
      const logs = await api.getExecutionLogs(execId);
      setExecutionLogs(logs);
    } catch (err: unknown) {
      console.error("Error loading execution logs:", err);
    }
  }, []);

  // 2. Lifecycle triggers
  useEffect(() => {
    const timer = setTimeout(() => {
      fetchProjects();
    }, 0);
    return () => clearTimeout(timer);
  }, [fetchProjects]);

  useEffect(() => {
    if (selectedProject) {
      const timer = setTimeout(() => {
        fetchWorkspaceData(selectedProject.id);
      }, 0);
      return () => clearTimeout(timer);
    }
  }, [selectedProject, fetchWorkspaceData]);

  useEffect(() => {
    if (selectedTask) {
      const timer = setTimeout(() => {
        fetchExecutions(selectedTask.id);
      }, 0);
      return () => clearTimeout(timer);
    } else {
      const timer = setTimeout(() => {
        setExecutions([]);
        setSelectedExecution(null);
        setExecutionLogs("");
      }, 0);
      return () => clearTimeout(timer);
    }
  }, [selectedTask, fetchExecutions]);

  // Log Polling Loop
  useEffect(() => {
    if (selectedExecution) {
      const timer = setTimeout(() => {
        fetchLogs(selectedExecution.id);
      }, 0);

      const isRunning = selectedExecution.status === "PENDING" || selectedExecution.status === "RUNNING";
      let interval: NodeJS.Timeout | null = null;
      if (isRunning) {
        interval = setInterval(() => {
          if (selectedExecution) {
            fetchExecutions(selectedTask!.id);
            fetchLogs(selectedExecution.id);
          }
        }, 1500);
      }

      return () => {
        clearTimeout(timer);
        if (interval) clearInterval(interval);
      };
    } else {
      const timer = setTimeout(() => {
        setExecutionLogs("");
      }, 0);
      return () => clearTimeout(timer);
    }
  }, [selectedExecution, selectedTask, fetchExecutions, fetchLogs]);

  // Auto scroll console
  useEffect(() => {
    if (terminalEndRef.current) {
      terminalEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [executionLogs]);

  // 3. Actions
  const handleRegisterProject = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormLoading(true);
    setFormError(null);
    setFormSuccess(false);

    try {
      await api.createProject({
        key,
        name,
        description,
        repositoryPath,
        defaultBranch,
        contextPath,
      });

      setFormSuccess(true);
      setKey("");
      setName("");
      setDescription("");
      setRepositoryPath("");
      setDefaultBranch("main");
      setContextPath(".ai");

      await fetchProjects();
    } catch (err: unknown) {
      const errorResp = err as ErrorResponse;
      let msg = errorResp.message || "Error al registrar el proyecto.";
      if (errorResp.details && errorResp.details.length > 0) {
        msg += " Detalles: " + errorResp.details.join(", ");
      }
      setFormError(msg);
    } finally {
      setFormLoading(false);
    }
  };

  const handleArchive = async (id: string) => {
    try {
      await api.archiveProject(id);
      await fetchProjects();
    } catch (err: unknown) {
      const errorResp = err as ErrorResponse;
      alert(errorResp.message || "No se pudo archivar el proyecto.");
    }
  };

  const handleRegisterAgent = async (e: React.FormEvent) => {
    e.preventDefault();
    setAgentFormError(null);
    try {
      await api.createAgent({
        name: agentName,
        role: agentRole,
        systemPrompt: agentPrompt,
        modelName: agentModel,
        temperature: agentTemp
      });
      setAgentName("");
      setAgentPrompt("");
      setAgentTemp(0.7);
      if (selectedProject) fetchWorkspaceData(selectedProject.id);
    } catch (err: unknown) {
      const errorResp = err as ErrorResponse;
      setAgentFormError(errorResp.message || "Error al registrar el agente.");
    }
  };

  const handleCreateTask = async (e: React.FormEvent) => {
    e.preventDefault();
    setTaskFormError(null);
    if (!selectedProject) return;

    try {
      await api.createTask(selectedProject.id, {
        title: taskTitle,
        description: taskDesc,
        assigneeId: taskAssignee || undefined,
        reviewerId: taskReviewer || undefined
      });
      setTaskTitle("");
      setTaskDesc("");
      setTaskAssignee("");
      setTaskReviewer("");
      fetchWorkspaceData(selectedProject.id);
    } catch (err: unknown) {
      const errorResp = err as ErrorResponse;
      setTaskFormError(errorResp.message || "Error al registrar la tarea.");
    }
  };

  const handleTaskStatusChange = async (status: TaskStatus) => {
    if (!selectedTask) return;
    try {
      const updated = await api.changeTaskStatus(selectedTask.id, status);
      setSelectedTask(updated);
      if (selectedProject) fetchWorkspaceData(selectedProject.id);
    } catch (err: unknown) {
      const errorResp = err as ErrorResponse;
      alert(errorResp.message || "Error al cambiar el estado de la tarea.");
    }
  };

  const handleTriggerExecution = async (e: React.FormEvent) => {
    e.preventDefault();
    setExecFormError(null);
    if (!selectedTask) return;

    try {
      const trigger = await api.triggerExecution(selectedTask.id, {
        agentId: execAgentId,
        commandLine: execCmdLine
      });
      setExecCmdLine("");
      setSelectedExecution(trigger);
      fetchExecutions(selectedTask.id);
    } catch (err: unknown) {
      const errorResp = err as ErrorResponse;
      setExecFormError(errorResp.message || "Error al lanzar la ejecución.");
    }
  };

  const handleApproveExecution = async (id: string) => {
    try {
      const approved = await api.approveExecution(id);
      setSelectedExecution(approved);
      if (selectedTask) fetchExecutions(selectedTask.id);
    } catch (err: unknown) {
      const errorResp = err as ErrorResponse;
      alert(errorResp.message || "Error al aprobar la ejecución.");
    }
  };

  const handleRejectExecution = async (id: string) => {
    try {
      const rejected = await api.rejectExecution(id);
      setSelectedExecution(rejected);
      if (selectedTask) fetchExecutions(selectedTask.id);
    } catch (err: unknown) {
      const errorResp = err as ErrorResponse;
      alert(errorResp.message || "Error al rechazar la ejecución.");
    }
  };

  const getStatusStyle = (status: string) => {
    switch (status) {
      case "DRAFT":
        return "bg-slate-800 text-slate-300 border border-slate-700";
      case "ACTIVE":
      case "READY":
        return "bg-emerald-950/80 text-emerald-300 border border-emerald-800/80";
      case "IN_PROGRESS":
      case "RUNNING":
        return "bg-indigo-950/80 text-indigo-300 border border-indigo-800/80 animate-pulse";
      case "NEEDS_REVIEW":
      case "WAITING_APPROVAL":
        return "bg-amber-950/80 text-amber-300 border border-amber-800/80 font-bold";
      case "DONE":
      case "SUCCESS":
        return "bg-emerald-900/60 text-emerald-200 border border-emerald-700/60";
      case "FAILED":
        return "bg-rose-950/80 text-rose-300 border border-rose-800/80";
      default:
        return "bg-slate-800 text-slate-300";
    }
  };

  return (
    <div className="bg-gradient-to-br from-slate-900 via-slate-950 to-indigo-950 text-white min-h-screen font-sans">
      
      {/* Top Header navbar */}
      <header className="border-b border-slate-800 bg-slate-900/60 backdrop-blur-md sticky top-0 z-50 px-8 py-4 flex items-center justify-between shadow-lg">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-indigo-500 to-violet-600 flex items-center justify-center shadow-indigo-500/30 shadow-lg">
            <span className="font-bold text-lg text-white">O</span>
          </div>
          <div>
            <h1 className="font-extrabold text-xl tracking-tight bg-gradient-to-r from-white via-slate-100 to-indigo-400 bg-clip-text text-transparent">
              Orquestador de Agentes IA
            </h1>
            <p className="text-xs text-slate-400">Control de Agentes Autónomos Multiproyecto</p>
          </div>
        </div>

        {selectedProject && (
          <button
            onClick={() => {
              setSelectedProject(null);
              setSelectedTask(null);
              setSelectedExecution(null);
            }}
            className="flex items-center gap-2 px-4 py-2 text-sm font-semibold rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 transition-all border border-slate-700 shadow-md cursor-pointer"
          >
            ← Proyectos
          </button>
        )}
      </header>

      {/* Main Container */}
      <main className="max-w-7xl mx-auto px-6 py-8">
        
        {/* PAGE 1: Projects Overview (If no project selected) */}
        {!selectedProject ? (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            
            {/* Left side: Project Creator form */}
            <div className="lg:col-span-1 bg-slate-900/60 backdrop-blur-md border border-slate-800/80 p-6 rounded-2xl shadow-xl">
              <h2 className="text-lg font-bold mb-4 bg-gradient-to-r from-violet-400 to-indigo-300 bg-clip-text text-transparent">
                Registrar Proyecto
              </h2>
              
              <form onSubmit={handleRegisterProject} className="space-y-4 text-sm">
                <div>
                  <label className="block text-slate-400 font-medium mb-1">Clave Única (Key)</label>
                  <input
                    type="text"
                    required
                    placeholder="Ej. MI-PROYECTO"
                    value={key}
                    onChange={(e) => setKey(e.target.value.toLowerCase())}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 focus:outline-none focus:border-indigo-500 text-white"
                  />
                </div>

                <div>
                  <label className="block text-slate-400 font-medium mb-1">Nombre</label>
                  <input
                    type="text"
                    required
                    placeholder="Ej. MiroFish Core"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 focus:outline-none focus:border-indigo-500 text-white"
                  />
                </div>

                <div>
                  <label className="block text-slate-400 font-medium mb-1">Descripción</label>
                  <textarea
                    placeholder="Detalles sobre el proyecto..."
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 h-16 resize-none focus:outline-none focus:border-indigo-500 text-white"
                  />
                </div>

                <div>
                  <label className="block text-slate-400 font-medium mb-1">Ruta Física del Repositorio</label>
                  <input
                    type="text"
                    required
                    placeholder="/absolute/path/to/project"
                    value={repositoryPath}
                    onChange={(e) => setRepositoryPath(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 focus:outline-none focus:border-indigo-500 text-white"
                  />
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-slate-400 font-medium mb-1">Rama Default</label>
                    <input
                      type="text"
                      value={defaultBranch}
                      onChange={(e) => setDefaultBranch(e.target.value)}
                      className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 focus:outline-none focus:border-indigo-500 text-white"
                    />
                  </div>
                  <div>
                    <label className="block text-slate-400 font-medium mb-1">Path Contexto</label>
                    <input
                      type="text"
                      value={contextPath}
                      onChange={(e) => setContextPath(e.target.value)}
                      className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 focus:outline-none focus:border-indigo-500 text-white"
                    />
                  </div>
                </div>

                {formError && (
                  <div className="p-3 bg-rose-950/60 border border-rose-800/80 rounded-lg text-rose-300 text-xs">
                    {formError}
                  </div>
                )}

                {formSuccess && (
                  <div className="p-3 bg-emerald-950/60 border border-emerald-800/80 rounded-lg text-emerald-300 text-xs">
                    ¡Proyecto registrado con éxito!
                  </div>
                )}

                <button
                  type="submit"
                  disabled={formLoading}
                  className="w-full bg-gradient-to-r from-indigo-500 to-violet-600 hover:from-indigo-600 hover:to-violet-700 text-white font-bold py-2.5 px-4 rounded-lg shadow-lg hover:shadow-indigo-500/20 transition-all text-sm cursor-pointer disabled:opacity-50"
                >
                  {formLoading ? "Registrando..." : "Añadir Proyecto"}
                </button>
              </form>
            </div>

            {/* Right side: Project Grid list */}
            <div className="lg:col-span-2 space-y-6">
              <div className="flex justify-between items-center">
                <h2 className="text-xl font-bold tracking-tight">Proyectos Registrados</h2>
                <span className="text-xs bg-slate-800 border border-slate-700 px-3 py-1 rounded-full text-slate-300 font-semibold">
                  {projects.length} Registros
                </span>
              </div>

              {loading ? (
                <div className="flex justify-center items-center h-48">
                  <div className="w-8 h-8 rounded-full border-4 border-indigo-500 border-t-transparent animate-spin"></div>
                </div>
              ) : error ? (
                <div className="p-4 bg-rose-950/40 border border-rose-900/60 rounded-xl text-rose-200 text-sm">
                  {error}
                </div>
              ) : projects.length === 0 ? (
                <div className="p-12 text-center bg-slate-900/40 border border-slate-855 rounded-2xl">
                  <p className="text-slate-400">No hay proyectos registrados en el orquestador.</p>
                  <p className="text-xs text-slate-500 mt-1">Utiliza el formulario lateral para agregar tu primer proyecto.</p>
                </div>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {projects.map((proj) => (
                    <div
                      key={proj.id}
                      onClick={() => setSelectedProject(proj)}
                      className="bg-slate-900/40 border border-slate-800/80 p-5 rounded-2xl hover:border-violet-500/80 hover:bg-slate-900/70 transition-all duration-300 cursor-pointer shadow-md group flex flex-col justify-between"
                    >
                      <div>
                        <div className="flex justify-between items-start mb-2">
                          <span className="text-xs font-mono font-bold bg-slate-800 text-indigo-300 border border-slate-700 px-2 py-0.5 rounded">
                            {proj.key}
                          </span>
                          <span className={`text-[10px] px-2 py-0.5 rounded-full font-bold ${getStatusStyle(proj.status)}`}>
                            {proj.status}
                          </span>
                        </div>
                        <h3 className="text-base font-bold text-slate-100 group-hover:text-indigo-300 transition-colors">
                          {proj.name}
                        </h3>
                        <p className="text-xs text-slate-400 mt-1.5 line-clamp-2">
                          {proj.description || "Sin descripción proporcionada."}
                        </p>
                      </div>

                      <div className="border-t border-slate-855 pt-3 mt-4 text-[11px] text-slate-500 font-mono">
                        <div className="flex justify-between items-end">
                          <div className="space-y-1">
                            <div><strong className="text-slate-400">Repo:</strong> {proj.repositoryPath}</div>
                            <div><strong className="text-slate-400">Rama:</strong> {proj.defaultBranch}</div>
                          </div>
                          <button
                            onClick={(e) => {
                              e.stopPropagation();
                              handleArchive(proj.id);
                            }}
                            className="bg-slate-800 hover:bg-rose-950/60 hover:text-rose-200 border border-slate-700 hover:border-rose-900 px-2 py-1 rounded text-[10px] text-slate-300 font-bold transition-all cursor-pointer"
                          >
                            Archivar
                          </button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>

          </div>
        ) : (
          
          /* PAGE 2: Workspace Project Details (Active project view) */
          <div className="space-y-6">
            
            {/* Top Glassmorphic Project Brief */}
            <div className="bg-slate-900/60 backdrop-blur-md border border-slate-850 p-6 rounded-2xl shadow-xl flex flex-col md:flex-row md:items-center justify-between gap-4">
              <div>
                <div className="flex items-center gap-3 mb-1">
                  <span className="text-xs font-mono font-bold bg-indigo-950 text-indigo-300 border border-indigo-800 px-2 py-0.5 rounded">
                    {selectedProject.key}
                  </span>
                  <span className={`text-[10px] px-2 py-0.5 rounded-full font-bold ${getStatusStyle(selectedProject.status)}`}>
                    {selectedProject.status}
                  </span>
                </div>
                <h2 className="text-xl font-extrabold text-slate-100">{selectedProject.name}</h2>
                <p className="text-xs text-slate-400 mt-1">{selectedProject.description || "Sin descripción."}</p>
                <div className="text-[11px] text-slate-500 font-mono mt-2">
                  Ruta Física: <span className="text-slate-400">{selectedProject.repositoryPath}</span>
                </div>
              </div>

              {/* Tab Navigation */}
              <div className="flex bg-slate-950 p-1.5 rounded-xl border border-slate-855">
                <button
                  onClick={() => setActiveTab("tasks")}
                  className={`px-4 py-1.5 text-xs font-bold rounded-lg transition-all cursor-pointer ${
                    activeTab === "tasks" ? "bg-indigo-600 text-white shadow" : "text-slate-400 hover:text-slate-200"
                  }`}
                >
                  Tareas ({tasks.length})
                </button>
                <button
                  onClick={() => setActiveTab("agents")}
                  className={`px-4 py-1.5 text-xs font-bold rounded-lg transition-all cursor-pointer ${
                    activeTab === "agents" ? "bg-indigo-600 text-white shadow" : "text-slate-400 hover:text-slate-200"
                  }`}
                >
                  Agentes ({agents.length})
                </button>
              </div>
            </div>

            {/* TAB CONTENT: AGENTS VIEW */}
            {activeTab === "agents" && (
              <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                
                {/* Agent Creator */}
                <div className="lg:col-span-1 bg-slate-900/60 border border-slate-850 p-6 rounded-2xl shadow-xl">
                  <h3 className="text-base font-bold mb-4 bg-gradient-to-r from-violet-400 to-indigo-300 bg-clip-text text-transparent">
                    Registrar Agente de IA
                  </h3>

                  <form onSubmit={handleRegisterAgent} className="space-y-4 text-xs">
                    <div>
                      <label className="block text-slate-400 font-medium mb-1">Nombre</label>
                      <input
                        type="text"
                        required
                        placeholder="Ej. DeveloperAgent"
                        value={agentName}
                        onChange={(e) => setAgentName(e.target.value)}
                        className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 focus:outline-none focus:border-indigo-500 text-white"
                      />
                    </div>

                    <div>
                      <label className="block text-slate-400 font-medium mb-1">Rol</label>
                      <select
                        value={agentRole}
                        onChange={(e) => setAgentRole(e.target.value)}
                        className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 focus:outline-none focus:border-indigo-500 text-white"
                      >
                        <option value="DEVELOPER">DEVELOPER (Programador)</option>
                        <option value="REVIEWER">REVIEWER (Revisor)</option>
                        <option value="AUDITOR">AUDITOR (Auditor de Calidad)</option>
                      </select>
                    </div>

                    <div>
                      <label className="block text-slate-400 font-medium mb-1">Modelo de Lenguaje</label>
                      <input
                        type="text"
                        required
                        placeholder="gemini-1.5-flash"
                        value={agentModel}
                        onChange={(e) => setAgentModel(e.target.value)}
                        className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 focus:outline-none focus:border-indigo-500 text-white"
                      />
                    </div>

                    <div>
                      <label className="block text-slate-400 font-medium mb-1">Temperatura: {agentTemp}</label>
                      <input
                        type="range"
                        min="0"
                        max="1.5"
                        step="0.1"
                        value={agentTemp}
                        onChange={(e) => setAgentTemp(parseFloat(e.target.value))}
                        className="w-full accent-indigo-500 bg-slate-950 rounded-lg focus:outline-none"
                      />
                    </div>

                    <div>
                      <label className="block text-slate-400 font-medium mb-1">System Prompt</label>
                      <textarea
                        placeholder="Instrucciones del rol del agente..."
                        value={agentPrompt}
                        onChange={(e) => setAgentPrompt(e.target.value)}
                        className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 h-20 resize-none focus:outline-none focus:border-indigo-500 text-white"
                      />
                    </div>

                    {agentFormError && (
                      <div className="p-3 bg-rose-950/60 border border-rose-800/80 rounded-lg text-rose-300 text-xs">
                        {agentFormError}
                      </div>
                    )}

                    <button
                      type="submit"
                      className="w-full bg-gradient-to-r from-indigo-500 to-violet-600 hover:from-indigo-600 hover:to-violet-700 text-white font-bold py-2 px-4 rounded-lg shadow-md transition-all cursor-pointer"
                    >
                      Registrar Agente
                    </button>
                  </form>
                </div>

                {/* Agents List */}
                <div className="lg:col-span-2 space-y-4">
                  <h3 className="text-lg font-bold">Listado de Agentes</h3>

                  {agents.length === 0 ? (
                    <div className="p-8 text-center bg-slate-900/40 border border-slate-855 rounded-2xl text-slate-400">
                      No hay agentes configurados para este orquestador.
                    </div>
                  ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      {agents.map((ag) => (
                        <div
                          key={ag.id}
                          className="bg-slate-900/40 border border-slate-800/80 p-4 rounded-xl flex flex-col justify-between"
                        >
                          <div>
                            <div className="flex justify-between items-center mb-1">
                              <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-indigo-950 text-indigo-300 border border-indigo-800">
                                {ag.role}
                              </span>
                              <span className="text-[10px] text-slate-500 font-mono">Temp: {ag.temperature}</span>
                            </div>
                            <h4 className="font-bold text-slate-200 text-sm">{ag.name}</h4>
                            <p className="text-[11px] text-slate-400 mt-2 font-mono">
                              <strong>Modelo:</strong> {ag.modelName}
                            </p>
                            {ag.systemPrompt && (
                              <p className="text-[11px] text-slate-500 mt-1 line-clamp-2 italic">
                                &quot;{ag.systemPrompt}&quot;
                              </p>
                            )}
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>

              </div>
            )}

            {/* TAB CONTENT: TASKS WORKSPACE VIEW */}
            {activeTab === "tasks" && (
              <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                
                {/* Left workspace side: Tasks list and creator */}
                <div className="lg:col-span-1 space-y-6">
                  
                  {/* Task Creator form */}
                  <div className="bg-slate-900/60 border border-slate-850 p-5 rounded-2xl shadow-md">
                    <h3 className="text-sm font-bold mb-3 text-slate-200">Crear Contrato de Tarea</h3>
                    
                    <form onSubmit={handleCreateTask} className="space-y-3 text-xs">
                      <div>
                        <label className="block text-slate-400 font-medium mb-1">Título</label>
                        <input
                          type="text"
                          required
                          placeholder="Ej. TASK_006: Orchestrator Domain"
                          value={taskTitle}
                          onChange={(e) => setTaskTitle(e.target.value)}
                          className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 focus:outline-none focus:border-indigo-500 text-white"
                        />
                      </div>

                      <div>
                        <label className="block text-slate-400 font-medium mb-1">Descripción</label>
                        <textarea
                          placeholder="Criterios y objetivos de la tarea..."
                          value={taskDesc}
                          onChange={(e) => setTaskDesc(e.target.value)}
                          className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 h-14 resize-none focus:outline-none focus:border-indigo-500 text-white"
                        />
                      </div>

                      <div className="grid grid-cols-2 gap-3">
                        <div>
                          <label className="block text-slate-400 font-medium mb-1">Asignado</label>
                          <select
                            value={taskAssignee}
                            onChange={(e) => setTaskAssignee(e.target.value)}
                            className="w-full bg-slate-950 border border-slate-800 rounded-lg p-1.5 focus:outline-none focus:border-indigo-500 text-white text-[11px]"
                          >
                            <option value="">(Sin asignar)</option>
                            {agents.map(ag => (
                              <option key={ag.id} value={ag.id}>{ag.name} ({ag.role})</option>
                            ))}
                          </select>
                        </div>
                        <div>
                          <label className="block text-slate-400 font-medium mb-1">Revisor</label>
                          <select
                            value={taskReviewer}
                            onChange={(e) => setTaskReviewer(e.target.value)}
                            className="w-full bg-slate-950 border border-slate-800 rounded-lg p-1.5 focus:outline-none focus:border-indigo-500 text-white text-[11px]"
                          >
                            <option value="">(Sin asignar)</option>
                            {agents.map(ag => (
                              <option key={ag.id} value={ag.id}>{ag.name} ({ag.role})</option>
                            ))}
                          </select>
                        </div>
                      </div>

                      {taskFormError && (
                        <div className="p-2 bg-rose-950/60 border border-rose-800/80 rounded-lg text-rose-300 text-[10px]">
                          {taskFormError}
                        </div>
                      )}

                      <button
                        type="submit"
                        className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 rounded-lg transition-all cursor-pointer"
                      >
                        Añadir Tarea
                      </button>
                    </form>
                  </div>

                  {/* Tasks List */}
                  <div className="space-y-3">
                    <h3 className="text-sm font-bold text-slate-300">Contratos de Tarea</h3>

                    {tasks.length === 0 ? (
                      <div className="p-8 text-center bg-slate-900/40 border border-slate-850 rounded-xl text-xs text-slate-400">
                        No hay tareas creadas para este proyecto.
                      </div>
                    ) : (
                      <div className="space-y-2">
                        {tasks.map((tk) => {
                          const devAgent = agents.find(a => a.id === tk.assigneeId);
                          return (
                            <div
                              key={tk.id}
                              onClick={() => {
                                setSelectedTask(tk);
                                setSelectedExecution(null);
                              }}
                              className={`p-3 rounded-xl border cursor-pointer transition-all ${
                                selectedTask?.id === tk.id
                                  ? "bg-indigo-950/50 border-indigo-500 shadow-md shadow-indigo-500/5"
                                  : "bg-slate-900/30 border-slate-855 hover:border-slate-700"
                              }`}
                            >
                              <div className="flex justify-between items-start gap-2 mb-1">
                                <span className={`text-[9px] px-1.5 py-0.5 rounded font-bold ${getStatusStyle(tk.status)}`}>
                                  {tk.status}
                                </span>
                                {devAgent && (
                                  <span className="text-[9px] text-indigo-400 font-semibold">
                                    @{devAgent.name}
                                  </span>
                                )}
                              </div>
                              <h4 className="text-xs font-bold text-slate-200 line-clamp-1">{tk.title}</h4>
                            </div>
                          );
                        })}
                      </div>
                    )}
                  </div>

                </div>

                {/* Right workspace side: Active Task details and Execution Panel */}
                <div className="lg:col-span-2 space-y-6">
                  {selectedTask ? (
                    <div className="bg-slate-900/60 border border-slate-850 p-6 rounded-2xl shadow-xl space-y-6">
                      
                      {/* Task Info header */}
                      <div className="border-b border-slate-850 pb-4 flex flex-col md:flex-row md:items-center justify-between gap-4">
                        <div>
                          <div className="flex items-center gap-2 mb-1">
                            <span className={`text-[10px] px-2 py-0.5 rounded font-bold ${getStatusStyle(selectedTask.status)}`}>
                              {selectedTask.status}
                            </span>
                            <span className="text-xs text-slate-500">ID: {selectedTask.id.substring(0, 8)}...</span>
                          </div>
                          <h3 className="text-lg font-bold text-slate-100">{selectedTask.title}</h3>
                          <p className="text-xs text-slate-400 mt-1">{selectedTask.description || "Sin descripción."}</p>
                        </div>

                        {/* Status transition actions */}
                        <div className="flex items-center gap-2">
                          <label className="text-xs text-slate-400">Estado:</label>
                          <select
                            value={selectedTask.status}
                            onChange={(e) => handleTaskStatusChange(e.target.value as TaskStatus)}
                            className="bg-slate-950 border border-slate-800 rounded-lg px-2.5 py-1.5 text-xs text-slate-200 focus:outline-none focus:border-indigo-500"
                          >
                            <option value="DRAFT">DRAFT</option>
                            <option value="READY">READY</option>
                            <option value="IN_PROGRESS">IN_PROGRESS</option>
                            <option value="NEEDS_REVIEW">NEEDS_REVIEW</option>
                            <option value="DONE">DONE</option>
                          </select>
                        </div>
                      </div>

                      {/* Execution triggers & history grid */}
                      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                        
                        {/* Execute Form (Left column) */}
                        <div className="md:col-span-1 space-y-4">
                          <h4 className="text-xs font-extrabold text-slate-400 uppercase tracking-wider">
                            Lanzar Comando
                          </h4>

                          <form onSubmit={handleTriggerExecution} className="space-y-3 text-xs">
                            <div>
                              <label className="block text-slate-400 mb-1">Agente Emisor</label>
                              <select
                                required
                                value={execAgentId}
                                onChange={(e) => setExecAgentId(e.target.value)}
                                className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 focus:outline-none focus:border-indigo-500 text-white"
                              >
                                <option value="">(Selecciona Agente)</option>
                                {agents.map(ag => (
                                  <option key={ag.id} value={ag.id}>{ag.name}</option>
                                ))}
                              </select>
                            </div>

                            <div>
                              <label className="block text-slate-400 mb-1">Comando CLI (Terminal)</label>
                              <input
                                type="text"
                                required
                                placeholder="Ej. echo 'Hello orquestador'"
                                value={execCmdLine}
                                onChange={(e) => setExecCmdLine(e.target.value)}
                                className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2 focus:outline-none focus:border-indigo-500 text-white"
                              />
                            </div>

                            {execFormError && (
                              <div className="p-2 bg-rose-950/60 border border-rose-800/80 rounded-lg text-rose-300 text-[10px]">
                                {execFormError}
                              </div>
                            )}

                            <button
                              type="submit"
                              className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 rounded-lg transition-all cursor-pointer shadow-md"
                            >
                              Ejecutar
                            </button>
                          </form>
                        </div>

                        {/* Executions List (Right column) */}
                        <div className="md:col-span-2 space-y-3">
                          <h4 className="text-xs font-extrabold text-slate-400 uppercase tracking-wider">
                            Historial de Ejecuciones
                          </h4>

                          {executions.length === 0 ? (
                            <div className="p-6 text-center bg-slate-950/40 border border-slate-855 rounded-xl text-xs text-slate-505">
                              No hay ejecuciones registradas para esta tarea.
                            </div>
                          ) : (
                            <div className="space-y-2 max-h-48 overflow-y-auto pr-1">
                              {executions.map((ex) => {
                                const execAgent = agents.find(a => a.id === ex.agentId);
                                return (
                                  <div
                                    key={ex.id}
                                    onClick={() => setSelectedExecution(ex)}
                                    className={`p-2.5 rounded-lg border text-xs cursor-pointer flex justify-between items-center transition-all ${
                                      selectedExecution?.id === ex.id
                                        ? "bg-slate-800/60 border-indigo-500"
                                        : "bg-slate-950/60 border-slate-855 hover:border-slate-800"
                                    }`}
                                  >
                                    <div className="space-y-1">
                                      <div className="flex items-center gap-2">
                                        <span className={`text-[8px] font-bold px-1.5 py-0.5 rounded ${getStatusStyle(ex.status)}`}>
                                          {ex.status}
                                        </span>
                                        <span className="font-mono text-[10px] text-slate-300">
                                          {ex.commandLine}
                                        </span>
                                      </div>
                                      <div className="text-[10px] text-slate-500 font-mono">
                                        Agente: <span className="text-slate-400">@{execAgent?.name || "Desconocido"}</span>
                                      </div>
                                    </div>
                                    
                                    {ex.exitCode !== undefined && ex.exitCode !== null && (
                                      <span className={`font-mono text-[10px] font-bold ${ex.exitCode === 0 ? "text-emerald-400" : "text-rose-400"}`}>
                                        Exit: {ex.exitCode}
                                      </span>
                                    )}
                                  </div>
                                );
                              })}
                            </div>
                          )}
                        </div>

                      </div>

                      {/* Monospace terminal console (If execution selected) */}
                      {selectedExecution ? (
                        <div className="space-y-3 pt-4 border-t border-slate-850">
                          
                          <div className="flex justify-between items-center">
                            <div>
                              <h4 className="text-xs font-extrabold text-slate-400 uppercase tracking-wider">
                                Consola de Ejecución
                              </h4>
                              <p className="text-[10px] text-slate-500 font-mono">ID: {selectedExecution.id}</p>
                            </div>
                            <span className={`text-[10px] font-bold px-2 py-0.5 rounded ${getStatusStyle(selectedExecution.status)}`}>
                              {selectedExecution.status}
                            </span>
                          </div>

                          {/* HUMAN GATE INTERACTIVE BANNER */}
                          {selectedExecution.status === "WAITING_APPROVAL" && (
                            <div className="p-4 bg-amber-955/65 border border-amber-800/80 rounded-xl space-y-3 shadow-lg shadow-amber-950/10">
                              <div className="flex items-center gap-2 text-amber-300 font-bold text-sm">
                                <span className="text-lg">⚠</span>
                                <span>ALERTA DE SEGURIDAD: COMANDO DE RIESGO INTERCEPTADO</span>
                              </div>
                              <p className="text-xs text-amber-200">
                                El comando ingresado coincide con un patrón restringido (ej. git push, deploy) y requiere aprobación del propietario humano antes de iniciarse en el host local.
                              </p>
                              <div className="bg-slate-950 border border-slate-900 rounded p-2 text-xs font-mono text-slate-300">
                                $ {selectedExecution.commandLine}
                              </div>
                              
                              <div className="flex gap-3 pt-1">
                                <button
                                  onClick={() => handleApproveExecution(selectedExecution.id)}
                                  className="bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-bold px-4 py-2 rounded-lg cursor-pointer shadow-md transition-all"
                                >
                                  Aprobar y Ejecutar
                                </button>
                                <button
                                  onClick={() => handleRejectExecution(selectedExecution.id)}
                                  className="bg-rose-600 hover:bg-rose-700 text-white text-xs font-bold px-4 py-2 rounded-lg cursor-pointer shadow-md transition-all"
                                >
                                  Rechazar Comando
                                </button>
                              </div>
                            </div>
                          )}

                          {/* Live Console Output screen */}
                          <div className="bg-slate-950 font-mono text-emerald-400 p-4 border border-slate-800 rounded-lg shadow-inner h-64 overflow-y-auto text-[11px] leading-relaxed whitespace-pre-wrap">
                            {executionLogs ? (
                              executionLogs
                            ) : (
                              <span className="text-slate-600 italic">
                                {selectedExecution.status === "WAITING_APPROVAL"
                                  ? "Consola bloqueada. Esperando aprobación de seguridad del usuario humano..."
                                  : "Cargando flujo de logs..."}
                              </span>
                            )}
                            <div ref={terminalEndRef} />
                          </div>

                        </div>
                      ) : (
                        <div className="p-8 text-center bg-slate-950/40 border border-slate-850 rounded-xl text-xs text-slate-500">
                          Selecciona una ejecución del historial para desplegar su consola de logs en tiempo real.
                        </div>
                      )}

                    </div>
                  ) : (
                    <div className="p-12 text-center bg-slate-900/40 border border-slate-850 rounded-2xl text-slate-400">
                      Selecciona una tarea de la lista de contratos para inspeccionar su panel y consola.
                    </div>
                  )}
                </div>

              </div>
            )}

          </div>
        )}

      </main>
    </div>
  );
}
