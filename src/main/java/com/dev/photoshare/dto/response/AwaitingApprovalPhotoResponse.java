package com.dev.photoshare.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AwaitingApprovalPhotoResponse {
    private int ownerId;
    private String creatorName;
    private  long  photoId;
    private String description;
    private List<String> tags;
    private LocalDateTime uploadDate;
    private String imgUrl;
}
