package com.dev.photoshare.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PhotoDetailResponse {
    private int ownerId;
    private String creatorName;
    private String description;
    private String ownerAvatar;
    private String photoUrl;
    private long likeCount;
    private long commentCount;
    private String slug;
    private List<String> tags;
    private boolean isLiked;
}
