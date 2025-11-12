package com.dev.photoshare.service.UserStatsService;

import com.dev.photoshare.entity.Users;

public interface IUserStatsService {
    void increasePostCount(Users user);
}
