package com.suitup.backend.common;

import jakarta.servlet.http.HttpServletRequest;
import com.suitup.backend.auth.InvalidCredentialsException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(
        BusinessException exception,
        HttpServletRequest request
    ) {
        HttpStatus status = exception instanceof InvalidCredentialsException
            ? HttpStatus.UNAUTHORIZED
            : exception instanceof ResourceNotFoundException
            ? HttpStatus.NOT_FOUND
            : exception instanceof DuplicateResourceException
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;
        return response(status, exception.getErrorCode().name(), exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
        MethodArgumentNotValidException exception,
        HttpServletRequest request
    ) {
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
            fields.putIfAbsent(error.getField(), error.getDefaultMessage())
        );
        return response(
            HttpStatus.BAD_REQUEST,
            ErrorCode.VALIDATION_FAILED.name(),
            "Existem campos inválidos no pedido",
            request,
            fields
        );
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiErrorResponse> handleMalformedRequest(
        Exception exception,
        HttpServletRequest request
    ) {
        return response(
            HttpStatus.BAD_REQUEST,
            ErrorCode.BAD_REQUEST.name(),
            "O pedido contém JSON ou parâmetros inválidos",
            request,
            Map.of()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataConflict(
        DataIntegrityViolationException exception,
        HttpServletRequest request
    ) {
        return response(
            HttpStatus.CONFLICT,
            ErrorCode.CONFLICT.name(),
            "A operação entra em conflito com dados já existentes",
            request,
            Map.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
        Exception exception,
        HttpServletRequest request
    ) {
        LOGGER.error("Unexpected backend error on {}", request.getRequestURI(), exception);
        return response(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_SERVER_ERROR",
            "Ocorreu um erro interno inesperado",
            request,
            Map.of()
        );
    }

    private ResponseEntity<ApiErrorResponse> response(
        HttpStatus status,
        String error,
        String message,
        HttpServletRequest request,
        Map<String, String> fieldErrors
    ) {
        ApiErrorResponse body = new ApiErrorResponse(
            OffsetDateTime.now(ZoneOffset.UTC),
            status.value(),
            error,
            message,
            request.getRequestURI(),
            fieldErrors
        );
        return ResponseEntity.status(status).body(body);
    }
}
