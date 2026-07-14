package com.orquestador.orchestrator.domain.exceptions;

public class InvalidProjectStateException extends RuntimeException {
    public InvalidProjectStateException(String message) {
        super(message);
    }
}
