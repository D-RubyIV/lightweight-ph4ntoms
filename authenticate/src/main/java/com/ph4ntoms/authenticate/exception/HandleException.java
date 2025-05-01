package com.ph4ntoms.authenticate.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class HandleException {
    private final MessageSource messageSource;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorResponse {
        @JsonProperty("statusCode")
        private int statusCode;
        
        @JsonProperty("message")
        private String message;
        
        @JsonProperty("timestamp")
        private long timestamp = System.currentTimeMillis();
        
        @JsonProperty("path")
        private String path;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class ValidationErrorResponse extends ErrorResponse {
        @JsonProperty("errors")
        private Map<String, String> errors;
    }

    private String getMessage(String code, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, code, locale);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        List<FieldError> fieldErrors = ex.getFieldErrors();

        fieldErrors.forEach(error -> {
            String field = error.getField();
            String code = error.getCode();
            String message;

            if ("typeMismatch".equals(code)) {
                message = getMessage("validation.typeMismatch", new Object[]{field});
            } else {
                message = getMessage(error.getDefaultMessage(), new Object[]{field});
            }
            errors.put(field, message);
        });

        ValidationErrorResponse response = new ValidationErrorResponse();
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        response.setMessage(getMessage("validation.failed", null));
        response.setPath(request.getDescription(false));
        response.setErrors(errors);

        log.warn("Validation failed: {}", errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AuthenticateException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticateException(
            AuthenticateException ex, WebRequest request) {
        ErrorResponse response = new ErrorResponse();
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        response.setMessage(ex.getMessage());
        response.setPath(request.getDescription(false));
        
        log.warn("Authentication error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        ErrorResponse response = new ErrorResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        
        if (ex instanceof BadCredentialsException) {
            response.setMessage(getMessage("auth.login.failed", null));
        } else {
            response.setMessage(getMessage("auth.unauthorized", null));
        }
        
        response.setPath(request.getDescription(false));
        log.warn("Authentication error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        ErrorResponse response = new ErrorResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN.value());
        response.setMessage(getMessage("auth.forbidden", null));
        response.setPath(request.getDescription(false));
        
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleSQLIntegrityConstraintViolationException(
            SQLIntegrityConstraintViolationException ex, WebRequest request) {
        String value = ex.getMessage().split("'")[1];
        ErrorResponse response = new ErrorResponse();
        response.setStatusCode(HttpStatus.BAD_REQUEST.value());
        response.setMessage(getMessage("error.database.constraint", new Object[]{value}));
        response.setPath(request.getDescription(false));
        
        log.error("Database constraint violation: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        ErrorResponse response = new ErrorResponse();
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(getMessage("error.internal", null));
        response.setPath(request.getDescription(false));
        
        log.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}