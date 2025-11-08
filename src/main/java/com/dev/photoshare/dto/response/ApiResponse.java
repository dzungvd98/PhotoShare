package com.dev.photoshare.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ApiResponse<T> {
    private String message;
    private T data;
    private int status;
}
