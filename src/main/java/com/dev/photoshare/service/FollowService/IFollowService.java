package com.dev.photoshare.service.FollowService;

public interface IFollowService {
    String followUser(String targetUsername);
    String unfollowUser(String targetUsername);
}
