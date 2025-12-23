package com.dev.photoshare.exception;

import com.dev.photoshare.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getAllErrors().forEach(error -> {
//            String field = ((FieldError) error).getField();
//            String message = error.getDefaultMessage();
//            errors.put(field, message);
//        });
//
//        ErrorResponse error =  ErrorResponse.builder()
//                .errorCode(HttpStatus.BAD_REQUEST.name())
//                .message("Validation failed")
//                .timestamp(LocalDateTime.now())
//                .errors(errors)
//                .build();
//
//        return ResponseEntity.badRequest().body(error);
//    }

//    @ExceptionHandler(UserNotFoundException.class)
//    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
//        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(BusinessException.class)
//    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
//        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
//    }
//
//    @ExceptionHandler(InvalidOtpException.class)
//    public ResponseEntity<ErrorResponse> handleInvalidOtp(InvalidOtpException ex) {
//        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(EmailAlreadyExistsException.class)
//    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
//        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
//    }
//
//    @ExceptionHandler(TokenRefreshException.class)
//    public ResponseEntity<ErrorResponse> handleTokenRefresh(TokenRefreshException ex) {
//        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
//    }
//
//    @ExceptionHandler(TokenAlreadyRevokedException.class)
//    public ResponseEntity<ErrorResponse> handleTokenAlreadyRevoked(TokenAlreadyRevokedException ex) {
//        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
//    }
//
//    @ExceptionHandler(TokenNotFoundException.class)
//    public ResponseEntity<ErrorResponse> handleTokenNotFound(TokenNotFoundException ex) {
//        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
//        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
//    }
//
////    @ExceptionHandler(Exception.class)
////    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
////        return buildErrorResponse("An unexpected error occurred: " + ex.getMessage(),
////                HttpStatus.INTERNAL_SERVER_ERROR);
////    }
//
//    @ExceptionHandler(IpRateLimitException.class)
//    public ResponseEntity<ErrorResponse> handleIpRateLimit(IpRateLimitException ex) {
//        return buildErrorResponse(ex.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
//    }
//
//    @ExceptionHandler(UserAttemptLimitException.class)
//    public ResponseEntity<ErrorResponse> handleUserAttemptLimit(UserAttemptLimitException ex) {
//        return buildErrorResponse(ex.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
//    }
//
//
//
//    @ExceptionHandler(RateLimitExceededException.class)
//    public ResponseEntity<ErrorResponse> handleRateLimitExceeded(RateLimitExceededException ex) {
//        log.warn("Rate limit exceeded: {}", ex.getMessage());
//
//        ErrorResponse error = ErrorResponse.builder()
//                .errorCode(ex.getErrorCode())
//                .message(ex.getMessage())
//                .retryAfter(ex.getRetryAfter())
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
//    }
//
//    @ExceptionHandler(ValidationException.class)
//    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
//        log.warn("Validation error: {}", ex.getMessage());
//
//        ErrorResponse error = ErrorResponse.builder()
//                .errorCode(ex.getErrorCode())
//                .message(ex.getMessage())
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
//    }
//
//    @ExceptionHandler(InvalidCredentialsException.class)
//    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
//        log.warn("Invalid credentials: {}", ex.getMessage());
//
//        ErrorResponse error = ErrorResponse.builder()
//                .errorCode(ex.getErrorCode())
//                .message(ex.getMessage())
//                .attemptsRemaining(ex.getAttemptsRemaining())
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
//    }
//
//    @ExceptionHandler(AccountLockedException.class)
//    public ResponseEntity<ErrorResponse> handleAccountLocked(AccountLockedException ex) {
//        log.warn("Account locked: {}", ex.getMessage());
//
//        ErrorResponse error = ErrorResponse.builder()
//                .errorCode(ex.getErrorCode())
//                .message(ex.getMessage())
//                .lockedUntil(ex.getLockedUntil())
//                .reason(ex.getReason())
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
//    }
//
//    @ExceptionHandler(AccountDisabledException.class)
//    public ResponseEntity<ErrorResponse> handleAccountDisabled(AccountDisabledException ex) {
//        log.warn("Account disabled: {}", ex.getMessage());
//
//        ErrorResponse error = ErrorResponse.builder()
//                .errorCode(ex.getErrorCode())
//                .message(ex.getMessage())
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
//    }
//
//    @ExceptionHandler(AccountNotVerifiedException.class)
//    public ResponseEntity<ErrorResponse> handleAccountNotVerified(AccountNotVerifiedException ex) {
//        log.warn("Account not verified: {}", ex.getMessage());
//
//        ErrorResponse error = ErrorResponse.builder()
//                .errorCode(ex.getErrorCode())
//                .message(ex.getMessage())
//                .resendVerificationUrl(ex.getResendVerificationUrl())
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
//    }
//
//    @ExceptionHandler(PasswordExpiredException.class)
//    public ResponseEntity<ErrorResponse> handlePasswordExpired(PasswordExpiredException ex) {
//        log.warn("Password expired: {}", ex.getMessage());
//
//        ErrorResponse error = ErrorResponse.builder()
//                .errorCode(ex.getErrorCode())
//                .message(ex.getMessage())
//                .resetPasswordUrl(ex.getResetPasswordUrl())
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
//            MethodArgumentNotValidException ex) {
//
//        List<String> errors = ex.getBindingResult()
//                .getAllErrors()
//                .stream()
//                .map(error -> {
//                    String fieldName = ((FieldError) error).getField();
//                    String message = error.getDefaultMessage();
//                    return fieldName + ": " + message;
//                })
//                .toList();
//
//        log.warn("Validation failed: {}", errors);
//
//        ErrorResponse error = ErrorResponse.builder()
//                .errorCode("INVALID_INPUT")
//                .message("Validation failed")
//                .details(errors)
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
//        log.error("Unexpected error occurred", ex);
//
//        ErrorResponse error = ErrorResponse.builder()
//                .errorCode("INTERNAL_ERROR")
//                .message("An unexpected error occurred. Please try again later.")
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//    }
//
//    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status) {
//        ErrorResponse error =  ErrorResponse.builder()
//                .errorCode(status.name())
//                .message(message)
//                .timestamp(LocalDateTime.now())
//                .build();
//        return ResponseEntity.status(status).body(error);
//    }

    /**
     * Xử lý tất cả custom exception kế thừa từ BaseException
     * Chỉ cần 1 handler duy nhất thay vì nhiều handler riêng lẻ
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex, WebRequest request) {
        log.error("Business exception [{}]: {} at {}",
                ex.getErrorCode(),
                ex.getMessage(),
                getPath(request),
                ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus().value())
                .error(ex.getHttpStatus().getReasonPhrase())
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .path(getPath(request))
                .metadata(ex.getMetadata())
                .build();

        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    /**
     * Xử lý validation exception từ @Valid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        List<ErrorResponse.ValidationError> validationErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String message = error.getDefaultMessage();
                    Object rejectedValue = ((FieldError) error).getRejectedValue();

                    return ErrorResponse.ValidationError.builder()
                            .field(fieldName)
                            .message(message)
                            .rejectedValue(rejectedValue)
                            .build();
                })
                .toList();

        // Log với chi tiết validation errors
        log.warn("Validation failed at {}: {} errors",
                getPath(request),
                validationErrors.size());

        validationErrors.forEach(e ->
                log.debug("  - {}: {}", e.getField(), e.getMessage())
        );

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .errorCode("VALIDATION_ERROR")
                .message("Dữ liệu đầu vào không hợp lệ")
                .path(getPath(request))
                .validationErrors(validationErrors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request
    ) {
        log.error("Illegal argument at {}: {}",
                getPath(request),
                ex.getMessage(),
                ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .errorCode("INVALID_ARGUMENT")
                .message(ex.getMessage())
                .path(getPath(request))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý Spring Security AuthenticationException
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            WebRequest request
    ) {
        log.warn("Authentication failed at {}: {}",
                getPath(request),
                ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .errorCode("AUTHENTICATION_FAILED")
                .message("Xác thực thất bại. Vui lòng kiểm tra thông tin đăng nhập.")
                .path(getPath(request))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Xử lý Spring Security AccessDeniedException
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request
    ) {
        log.warn("Access denied at {}: {}",
                getPath(request),
                ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .errorCode("ACCESS_DENIED")
                .message("Bạn không có quyền truy cập tài nguyên này.")
                .path(getPath(request))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Xử lý tất cả exception chưa được handle
     * Fallback handler cuối cùng - KHÔNG BAO GIỜ LEAK THÔNG TIN
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request
    ) {
        // Log đầy đủ để debug
        log.error("Unhandled exception at {}: {}",
                getPath(request),
                ex.getMessage(),
                ex);

        // KHÔNG LEAK thông tin ra ngoài
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .errorCode("INTERNAL_ERROR")
                .message("Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.")
                .path(getPath(request))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

}
