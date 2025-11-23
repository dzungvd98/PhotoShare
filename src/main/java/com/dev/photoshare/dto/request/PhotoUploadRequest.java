package com.dev.photoshare.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class PhotoUploadRequest {
    @NotEmpty(message = "description is required!")
    private String description;
    List<String> tags;
}
