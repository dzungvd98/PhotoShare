package com.dev.photoshare.service.FollowService;

import com.dev.photoshare.dto.response.FollowResponse;

public interface IFollowService {
    String followUser(Integer followerId, String targetUsername);
    String unfollowUser(Integer followerId, String targetUsername);
}
