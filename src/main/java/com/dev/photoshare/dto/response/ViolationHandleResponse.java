package com.dev.photoshare.dto.response;

import com.dev.photoshare.utils.enums.ActionType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ViolationHandleResponse {
    private ActionType violationAction;
    private String violationMessage;
}
