package com.dev.photoshare.dto.request;

import com.dev.photoshare.utils.enums.CommentType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentRequest {
    private String content;
    private CommentType commentType;
}
