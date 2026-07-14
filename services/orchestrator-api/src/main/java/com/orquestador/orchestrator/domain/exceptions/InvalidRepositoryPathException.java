package com.orquestador.orchestrator.domain.exceptions;

public class InvalidRepositoryPathException extends RuntimeException {
    public InvalidRepositoryPathException(String message) {
        super(message);
    }
}
