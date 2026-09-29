package com.example.orders.frontend.server.bff.supplier;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@RestControllerAdvice
public class SupplierExceptionHandler {

    private static final MediaType PROBLEM_JSON = MediaType.valueOf("application/problem+json");

    @ExceptionHandler(SupplierBffExceptions.BadRequest.class)
    public ResponseEntity<SupplierProblem> badRequest(SupplierBffExceptions.BadRequest exception,
            HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "invalid_request", exception.getMessage(), Map.of(), request);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public ResponseEntity<SupplierProblem> malformedRequest(Exception exception, HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "malformed_request", "The request is malformed", Map.of(), request);
    }

    @ExceptionHandler(SupplierBffExceptions.InvalidInput.class)
    public ResponseEntity<SupplierProblem> invalidInput(SupplierBffExceptions.InvalidInput exception,
            HttpServletRequest request) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "validation_failed", exception.getMessage(),
                exception.getErrors(), request);
    }

    @ExceptionHandler(SupplierBffExceptions.NotFound.class)
    public ResponseEntity<SupplierProblem> notFound(SupplierBffExceptions.NotFound exception,
            HttpServletRequest request) {
        return problem(HttpStatus.NOT_FOUND, "supplier_not_found", exception.getMessage(), Map.of(), request);
    }

    @ExceptionHandler(SupplierBffExceptions.Conflict.class)
    public ResponseEntity<SupplierProblem> conflict(SupplierBffExceptions.Conflict exception,
            HttpServletRequest request) {
        return problem(HttpStatus.CONFLICT, "supplier_code_conflict", exception.getMessage(), Map.of(), request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<SupplierProblem> methodNotAllowed(HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        if (exception.getSupportedHttpMethods() != null) {
            headers.setAllow(exception.getSupportedHttpMethods());
        }
        return problem(HttpStatus.METHOD_NOT_ALLOWED, "method_not_allowed", "HTTP method is not supported",
                Map.of(), request, headers);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<SupplierProblem> unsupportedMediaType(HttpMediaTypeNotSupportedException exception,
            HttpServletRequest request) {
        return problem(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "unsupported_media_type",
                "Content-Type must be application/json", Map.of(), request);
    }

    @ExceptionHandler(SupplierBffExceptions.DownstreamUnavailable.class)
    public ResponseEntity<SupplierProblem> downstreamUnavailable(
            SupplierBffExceptions.DownstreamUnavailable exception, HttpServletRequest request) {
        return problem(HttpStatus.BAD_GATEWAY, "supplier_service_unavailable",
                "The supplier service is temporarily unavailable", Map.of(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SupplierProblem> internalError(Exception exception, HttpServletRequest request) {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "internal_error",
                "An unexpected server error occurred", Map.of(), request);
    }

    private ResponseEntity<SupplierProblem> problem(HttpStatus status, String code, String detail,
            Map<String, String> errors, HttpServletRequest request) {
        return problem(status, code, detail, errors, request, new HttpHeaders());
    }

    private ResponseEntity<SupplierProblem> problem(HttpStatus status, String code, String detail,
            Map<String, String> errors, HttpServletRequest request, HttpHeaders headers) {
        SupplierProblem body = new SupplierProblem("about:blank", status.getReasonPhrase(), status.value(),
                detail, request.getRequestURI(), code, errors);
        headers.setContentType(PROBLEM_JSON);
        return new ResponseEntity<>(body, headers, status);
    }
}