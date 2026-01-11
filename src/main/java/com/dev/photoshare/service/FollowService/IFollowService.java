package com.dev.photoshare.service.FollowService;

import com.dev.photoshare.dto.response.FollowResponse;

public interface IFollowService {
    FollowResponse toggleFollow(int userId);
}
