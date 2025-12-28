package com.dev.photoshare.dto.request;

import com.dev.photoshare.utils.enums.TargetType;
import com.dev.photoshare.utils.enums.ViolationReason;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ReportViolationRequest {
    @NotNull(message = "Vui lòng cho biết kiểu đối tượng bạn muốn báo cáo")
    private TargetType targetType;

    @NotNull(message = "Đối tượng báo cáo không được bỏ trống")
    private long targetId;

    @NotNull(message = "Lý do không được bỏ trống")
    private ViolationReason violationReason;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(max = 2000, message = "Mô tả không được vượt quá 2000 ký tự")
    private String description;
}
