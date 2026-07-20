package com.orquestador.orchestrator.domain.exceptions;

public class WorkspaceSecurityException extends RuntimeException {
    public WorkspaceSecurityException(String message) {
        super(message);
    }
}
