package com.dev.photoshare.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FollowResponse {
    private String status;
    private Integer followerFollowingCount;
    private Integer followerFollowersCount;
    private Integer targetFollowingCount;
    private Integer targetFollowersCount;
}
