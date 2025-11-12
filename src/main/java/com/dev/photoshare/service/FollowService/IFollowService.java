package com.dev.photoshare.service.FollowService;

import com.dev.photoshare.dto.response.FollowResponse;

public interface IFollowService {
    String followUser(String targetUsername);
    String unfollowUser(String targetUsername);
    FollowResponse toggleFollow(String targetUsername);
}
