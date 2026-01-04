package com.dev.photoshare.dto.request;

import com.dev.photoshare.utils.enums.ModerationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PhotoReviewRequest {
    private String reason;

    @NotNull(message = "Vui lòng chọn trạng thái phê duyệt")
    private ModerationStatus moderationStatus;
}
