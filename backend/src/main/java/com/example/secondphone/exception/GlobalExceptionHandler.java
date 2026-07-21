package com.example.secondphone.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> validation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> fields = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> fields.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return response(HttpStatus.BAD_REQUEST, "輸入資料驗證失敗", request, "VALIDATION_ERROR", fields);
    }
    @ExceptionHandler(DuplicateResourceException.class)
    ResponseEntity<ApiErrorResponse> duplicate(DuplicateResourceException exception, HttpServletRequest request) { return response(HttpStatus.CONFLICT, exception.getMessage(), request, "DUPLICATE_RESOURCE", null); }
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiErrorResponse> missing(ResourceNotFoundException exception, HttpServletRequest request) { return response(HttpStatus.NOT_FOUND, exception.getMessage(), request, "NOT_FOUND", null); }
    @ExceptionHandler({BadCredentialsException.class, DisabledException.class})
    ResponseEntity<ApiErrorResponse> authentication(RuntimeException exception, HttpServletRequest request) { return response(HttpStatus.UNAUTHORIZED, "帳號或密碼錯誤，或帳號目前無法登入", request, "AUTHENTICATION_FAILED", null); }
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiErrorResponse> status(ResponseStatusException exception, HttpServletRequest request) { return response(HttpStatus.valueOf(exception.getStatusCode().value()), exception.getReason(), request, null, null); }
    @ExceptionHandler({BusinessException.class, IllegalArgumentException.class})
    ResponseEntity<ApiErrorResponse> business(RuntimeException exception, HttpServletRequest request) { return response(HttpStatus.BAD_REQUEST, exception.getMessage(), request, "BUSINESS_ERROR", null); }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiErrorResponse> unexpected(Exception exception, HttpServletRequest request) {
        log.error("未預期的 API 錯誤：{} {}", request.getMethod(), request.getRequestURI(), exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "伺服器暫時無法處理要求", request, "INTERNAL_ERROR", null);
    }
    private ResponseEntity<ApiErrorResponse> response(HttpStatus status, String message, HttpServletRequest request, String code, Map<String, String> fields) {
        return ResponseEntity.status(status).body(ApiErrorResponse.of(message == null ? status.getReasonPhrase() : message, request.getRequestURI(), code, fields == null ? Map.of() : fields));
    }
}
