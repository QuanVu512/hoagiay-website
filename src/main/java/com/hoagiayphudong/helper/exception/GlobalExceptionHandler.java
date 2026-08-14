package com.hoagiayphudong.helper.exception;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleResourceAlreadyExists(ResourceAlreadyExistsException exception) {
        return buildError(HttpStatus.CONFLICT, exception.getMessage(), List.of());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException exception) {
        return buildError(HttpStatus.NOT_FOUND, exception.getMessage(), List.of());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiError> handleBadRequest(RuntimeException exception) {
        return buildError(HttpStatus.BAD_REQUEST, exception.getMessage(), List.of());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleInvalidJson() {
        return buildError(
                HttpStatus.BAD_REQUEST,
                "Json gui len khong hop le",
                List.of("Kiem tra Content-Type application/json va cau truc body.")
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication() {
        return buildError(HttpStatus.UNAUTHORIZED, "Tên đăng nhập hoặc mật khẩu không đúng.", List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        List<String> details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return buildError(HttpStatus.BAD_REQUEST, "Dữ liệu gửi lên chưa hợp lệ", details);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation() {
        return buildError(HttpStatus.CONFLICT, "Dữ liệu đã tồn tại hoặc không hợp lệ.", List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception exception) {
        LOGGER.error("Unexpected application error", exception);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra trong hệ thống", List.of());
    }

    private ResponseEntity<ApiError> buildError(HttpStatus status, String message, List<String> details) {
        ApiError error = new ApiError(message, status.value(), details, OffsetDateTime.now());
        return ResponseEntity.status(status).body(error);
    }
}
