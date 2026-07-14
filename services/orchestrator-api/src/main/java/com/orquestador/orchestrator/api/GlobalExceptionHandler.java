package com.orquestador.orchestrator.api;

import com.orquestador.orchestrator.api.dtos.ErrorResponse;
import com.orquestador.orchestrator.domain.exceptions.DuplicateProjectKeyException;
import com.orquestador.orchestrator.domain.exceptions.InvalidProjectStateException;
import com.orquestador.orchestrator.domain.exceptions.InvalidRepositoryPathException;
import com.orquestador.orchestrator.domain.exceptions.ProjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectNotFound(ProjectNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                "PROJECT_NOT_FOUND",
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DuplicateProjectKeyException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateKey(DuplicateProjectKeyException ex) {
        ErrorResponse error = new ErrorResponse(
                "DUPLICATE_PROJECT_KEY",
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidRepositoryPathException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPath(InvalidRepositoryPathException ex) {
        ErrorResponse error = new ErrorResponse(
                "INVALID_REPOSITORY_PATH",
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidProjectStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidState(InvalidProjectStateException ex) {
        ErrorResponse error = new ErrorResponse(
                "INVALID_PROJECT_STATE",
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse error = new ErrorResponse(
                "BAD_REQUEST_PARAMETER",
                ex.getMessage(),
                LocalDateTime.now(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse error = new ErrorResponse(
                "VALIDATION_ERROR",
                "Los datos de entrada no superaron la validación.",
                LocalDateTime.now(),
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Ocurrió un error inesperado en el servidor.",
                LocalDateTime.now(),
                List.of(ex.getMessage() != null ? ex.getMessage() : ex.toString())
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
