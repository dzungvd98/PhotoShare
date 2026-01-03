package com.dev.photoshare.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponse {

    private long commentId;
    private long userId;
    private String userAvatar;
    private String userDisplayName;
    private LocalDateTime createdDate;
    private String commentContent;
    private String status;
    private Integer replyCount;
}
