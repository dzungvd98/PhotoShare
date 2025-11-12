package com.dev.photoshare.service.ProfileService;

import com.dev.photoshare.dto.response.ProfileResponse;
import com.dev.photoshare.entity.Profiles;
import com.dev.photoshare.entity.UserStats;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.exception.UserNotFoundException;
import com.dev.photoshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService implements IProfileService{
    private final UserRepository userRepository;

    @Override
    public ProfileResponse getUserProfileProfile(int userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Profiles profile = user.getProfile();
        UserStats userStats = user.getUserStats();

        String displayName = StringUtils.hasText(profile.getDisplayName())
                ? profile.getDisplayName().trim()
                : user.getUsername();

        return ProfileResponse.builder()
                .bio(profile.getBio())
                .posts(userStats.getPostCount())
                .avatarUrl(profile.getAvatarUrl())
                .displayName(displayName)
                .isVerified(profile.isVerified())
                .followers(userStats.getFollowersCount())
                .following(userStats.getFollowingCount())
                .build();
    }
}
