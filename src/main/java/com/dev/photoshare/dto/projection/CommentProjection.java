package com.dev.photoshare.dto.projection;


import java.time.LocalDateTime;

public interface CommentProjection {

    Long getCommentId();
    String getCommentContent();
    LocalDateTime getCreatedDate();

    Integer getUserId();
    String getUserDisplayName();
    String getUserAvatar();

    Long getReplyCount();
}
