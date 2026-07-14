"use client";

import React, { useState, useEffect, useCallback } from "react";
import { api, Project, ErrorResponse } from "../services/api";

export default function Home() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Form State
  const [key, setKey] = useState("");
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [repositoryPath, setRepositoryPath] = useState("");
  const [defaultBranch, setDefaultBranch] = useState("main");
  const [contextPath, setContextPath] = useState(".ai");

  // Form Feedback
  const [formLoading, setFormLoading] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);
  const [formSuccess, setFormSuccess] = useState(false);

  const fetchProjects = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await api.listProjects(false); // list non-archived by default
      setProjects(data);
    } catch (err: unknown) {
      const errorResp = err as ErrorResponse;
      setError(errorResp.message || "No se pudieron cargar los proyectos.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const timer = setTimeout(() => {
      fetchProjects();
    }, 0);
    return () => clearTimeout(timer);
  }, [fetchProjects]);

  const handleRegister = async (e: React.FormEvent) => {
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
      // Clear form
      setKey("");
      setName("");
      setDescription("");
      setRepositoryPath("");
      setDefaultBranch("main");
      setContextPath(".ai");

      // Refresh list
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

  const getStatusColor = (status: Project["status"]) => {
    switch (status) {
      case "DRAFT":
        return "bg-slate-100 text-slate-800 border-slate-200 dark:bg-slate-800 dark:text-slate-200 dark:border-slate-700";
      case "ACTIVE":
        return "bg-emerald-100 text-emerald-800 border-emerald-200 dark:bg-emerald-950 dark:text-emerald-300 dark:border-emerald-800";
      case "PAUSED":
        return "bg-amber-100 text-amber-800 border-amber-200 dark:bg-amber-950 dark:text-amber-300 dark:border-amber-800";
      case "BLOCKED":
        return "bg-rose-100 text-rose-800 border-rose-200 dark:bg-rose-950 dark:text-rose-300 dark:border-rose-800";
      case "ARCHIVED":
        return "bg-zinc-100 text-zinc-800 border-zinc-200 dark:bg-zinc-800 dark:text-zinc-400 dark:border-zinc-700";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  return (
    <main className="min-h-screen bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 p-8">
      <div className="max-w-7xl mx-auto space-y-8">
        
        {/* Header */}
        <header className="flex flex-col md:flex-row md:items-center justify-between border-b border-slate-200 dark:border-slate-800 pb-6 gap-4">
          <div>
            <h1 className="text-3xl font-extrabold tracking-tight bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent dark:from-blue-400 dark:to-indigo-400">
              Orquestador de Agentes IA
            </h1>
            <p className="text-slate-500 dark:text-slate-400 mt-1">
              Plano de control y registro de proyectos locales
            </p>
          </div>
          <div className="flex gap-2">
            <button
              onClick={fetchProjects}
              className="px-4 py-2 text-sm font-medium border border-slate-300 dark:border-slate-700 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
            >
              Sincronizar
            </button>
          </div>
        </header>

        {/* Content Layout */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          
          {/* Projects List Card */}
          <div className="lg:col-span-2 space-y-6">
            <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-sm p-6">
              <h2 className="text-xl font-bold mb-4">Proyectos Registrados</h2>

              {loading ? (
                /* Loading State Skeletons */
                <div className="space-y-4">
                  {[1, 2].map((n) => (
                    <div key={n} className="border border-slate-100 dark:border-slate-800 rounded-xl p-4 animate-pulse space-y-3">
                      <div className="h-4 bg-slate-200 dark:bg-slate-800 rounded w-1/4"></div>
                      <div className="h-3 bg-slate-200 dark:bg-slate-800 rounded w-3/4"></div>
                      <div className="h-3 bg-slate-200 dark:bg-slate-800 rounded w-1/2"></div>
                    </div>
                  ))}
                </div>
              ) : error ? (
                /* Error State */
                <div className="bg-rose-50 border border-rose-200 text-rose-800 dark:bg-rose-950/30 dark:border-rose-900 dark:text-rose-300 rounded-xl p-4 text-sm">
                  {error}
                </div>
              ) : projects.length === 0 ? (
                /* Empty State */
                <div className="text-center py-12 border-2 border-dashed border-slate-200 dark:border-slate-800 rounded-xl">
                  <p className="text-slate-400 dark:text-slate-500">No hay proyectos activos registrados.</p>
                  <p className="text-sm text-slate-400 dark:text-slate-500 mt-1">Usa el formulario para dar de alta uno nuevo.</p>
                </div>
              ) : (
                /* Projects List */
                <div className="space-y-4">
                  {projects.map((project) => (
                    <div
                      key={project.id}
                      className="border border-slate-200 dark:border-slate-800 hover:border-slate-300 dark:hover:border-slate-700 rounded-xl p-5 flex flex-col md:flex-row md:items-center justify-between gap-4 transition-all bg-slate-50/50 dark:bg-slate-900/50"
                    >
                      <div className="space-y-2">
                        <div className="flex items-center gap-2">
                          <span className="text-xs font-mono bg-slate-200 dark:bg-slate-800 px-2 py-0.5 rounded text-slate-600 dark:text-slate-300">
                            {project.key}
                          </span>
                          <span className={`text-xs font-semibold px-2 py-0.5 border rounded-full ${getStatusColor(project.status)}`}>
                            {project.status}
                          </span>
                        </div>
                        <h3 className="text-lg font-bold">{project.name}</h3>
                        {project.description && (
                          <p className="text-sm text-slate-600 dark:text-slate-400 max-w-lg">{project.description}</p>
                        )}
                        <div className="text-xs text-slate-400 space-y-1">
                          <p>
                            <span className="font-medium text-slate-500">Ruta:</span> {project.repositoryPath}
                          </p>
                          <p>
                            <span className="font-medium text-slate-500">Rama:</span> {project.defaultBranch} | <span className="font-medium text-slate-500">Control:</span> {project.contextPath}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        <button
                          onClick={() => handleArchive(project.id)}
                          className="px-3 py-1.5 text-xs font-medium text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-950/20 border border-transparent rounded-lg hover:border-rose-200 transition-colors"
                        >
                          Archivar
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>

          {/* Registration Form Card */}
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl shadow-sm p-6 h-fit">
            <h2 className="text-xl font-bold mb-4">Registrar Nuevo Proyecto</h2>
            <form onSubmit={handleRegister} className="space-y-4">
              
              <div>
                <label className="block text-sm font-medium mb-1">Clave Corta (key) *</label>
                <input
                  type="text"
                  required
                  placeholder="ej. mi-proyecto-web"
                  value={key}
                  onChange={(e) => setKey(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-300 dark:border-slate-700 rounded-lg bg-transparent focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Nombre *</label>
                <input
                  type="text"
                  required
                  placeholder="ej. Mi Proyecto Web"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-300 dark:border-slate-700 rounded-lg bg-transparent focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Descripción</label>
                <textarea
                  placeholder="Propósito del proyecto..."
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-300 dark:border-slate-700 rounded-lg bg-transparent focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm h-20"
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Ruta Física del Repositorio *</label>
                <input
                  type="text"
                  required
                  placeholder="ej. /home/usuario/proyectos/mi-web"
                  value={repositoryPath}
                  onChange={(e) => setRepositoryPath(e.target.value)}
                  className="w-full px-3 py-2 border border-slate-300 dark:border-slate-700 rounded-lg bg-transparent focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-sm font-medium mb-1">Rama Git</label>
                  <input
                    type="text"
                    value={defaultBranch}
                    onChange={(e) => setDefaultBranch(e.target.value)}
                    className="w-full px-3 py-2 border border-slate-300 dark:border-slate-700 rounded-lg bg-transparent focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Carpeta .ai</label>
                  <input
                    type="text"
                    value={contextPath}
                    onChange={(e) => setContextPath(e.target.value)}
                    className="w-full px-3 py-2 border border-slate-300 dark:border-slate-700 rounded-lg bg-transparent focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
                  />
                </div>
              </div>

              {/* Feedback messages */}
              {formError && (
                <div className="bg-rose-50 border border-rose-200 text-rose-800 dark:bg-rose-950/30 dark:border-rose-900 dark:text-rose-300 rounded-lg p-3 text-xs">
                  {formError}
                </div>
              )}
              {formSuccess && (
                <div className="bg-emerald-50 border border-emerald-200 text-emerald-800 dark:bg-emerald-950/30 dark:border-emerald-900 dark:text-emerald-300 rounded-lg p-3 text-xs">
                  Proyecto registrado con éxito.
                </div>
              )}

              <button
                type="submit"
                disabled={formLoading}
                className="w-full bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600 text-white font-semibold py-2 rounded-lg transition-colors text-sm disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {formLoading ? "Registrando..." : "Registrar"}
              </button>
            </form>
          </div>

        </div>

      </div>
    </main>
  );
}
