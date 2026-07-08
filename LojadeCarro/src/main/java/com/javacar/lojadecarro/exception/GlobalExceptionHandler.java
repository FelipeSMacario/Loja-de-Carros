package com.javacar.lojadecarro.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> genericException(Exception ex,
                                                          HttpServletRequest request) {
        var response = new ResponseError(
                "Erro interno do servidor",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now(ZONE),
                request.getRequestURI()
        );
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseError> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        var message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Erro de validação");

        var response = new ResponseError(
                message,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(ZONE),
                request.getRequestURI()
        );

        log.warn("Validation error: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseError> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex,
                                                                             HttpServletRequest request) {
        ResponseError response = new ResponseError(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(ZONE),
                request.getRequestURI()
        );
        log.warn("methodArgumentTypeMismatchException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseError> missingServletRequestParameterException(MissingServletRequestParameterException ex,
                                                                             HttpServletRequest request) {
        ResponseError response = new ResponseError(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(ZONE),
                request.getRequestURI()
        );
        log.warn("missingServletRequestParameterException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseError> businessException(BusinessException ex,
                                                           HttpServletRequest request) {
        ResponseError response = new ResponseError(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(ZONE),
                request.getRequestURI()
        );
        log.warn("businessException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ResponseError> securityException(SecurityException ex,
                                                           HttpServletRequest request) {
        ResponseError response = new ResponseError(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now(ZONE),
                request.getRequestURI()
        );
        log.warn("businessException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

}
