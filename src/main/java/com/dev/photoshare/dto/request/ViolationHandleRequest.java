package com.dev.photoshare.dto.request;

import com.dev.photoshare.utils.enums.ActionType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ViolationHandleRequest {
    @NotNull(message = "Vui lòng chọn hình thức xử lý")
    private ActionType violationAction;

    private String violationMessage;
}
