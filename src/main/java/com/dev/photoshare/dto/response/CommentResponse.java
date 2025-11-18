package com.dev.photoshare.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder
@Setter
public class CommentResponse {
    private long targetId;
    private String content;
    private LocalDateTime createdDate;
    private String status;
}
