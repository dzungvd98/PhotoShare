package com.dev.photoshare.dto.projection;

public interface PhotoFeedView {
    Long getPhotoId();

    String getTitle();

    String getPhotoUrl();
    String getDescription();

    Long getCreatorId();
    String getCreatorName();
    String getOwnerAvatar();

    long getLikeCount();
    long getCommentCount();
    String getSlug();
}
