package com.dev.photoshare.utils;


import com.dev.photoshare.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Utility class để build ResponseEntity dễ dàng hơn
 * Giúp code ngắn gọn và nhất quán
 */
public class ResponseEntityBuilder {

    /**
     * Success response với HTTP 200 OK
     */
    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * Success response với HTTP 200 OK và custom message
     */
    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(message, data));
    }

    /**
     * Success response với HTTP 201 Created
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo thành công", data));
    }

    /**
     * Success response với HTTP 201 Created và custom message
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(message, data));
    }

    /**
     * Success response với HTTP 204 No Content
     */
    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    /**
     * Success response với HTTP 202 Accepted
     */
    public static <T> ResponseEntity<ApiResponse<T>> accepted(String message, T data) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success(message, data));
    }

    /**
     * Success response với HTTP 200 OK - không có data
     */
    public static ResponseEntity<ApiResponse<Void>> ok(String message) {
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> okWithHeader(
            String headerName,
            String headerValue,
            T data
    ) {
        return ResponseEntity.ok()
                .header(headerName, headerValue)
                .body(ApiResponse.success(data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> okWithHeader(
            String headerName,
            String headerValue,
            String message,
            T data
    ) {
        return ResponseEntity.ok()
                .header(headerName, headerValue)
                .body(ApiResponse.success(message, data));
    }
}