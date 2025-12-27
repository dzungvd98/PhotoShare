package com.dev.photoshare.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
public class PhotoUpdateRequest {

    @NotEmpty(message = "Tiêu đề ảnh không được bỏ trống")
    @Size(min = 5, max = 255, message = "Tiêu đề ảnh phải từ 5 đến 255 ký tự")
    private String title;

    @NotEmpty(message = "Mô tả ảnh không được bỏ trống")
    @Size(max = 2000, message = "Mô tả ảnh không được vượt quá 2000 ký tự")
    private String description;

    private List<String> tags;
}
