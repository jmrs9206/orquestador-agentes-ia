package com.orquestador.orchestrator.domain.exceptions;

public class DuplicateProjectKeyException extends RuntimeException {
    public DuplicateProjectKeyException(String message) {
        super(message);
    }
}
