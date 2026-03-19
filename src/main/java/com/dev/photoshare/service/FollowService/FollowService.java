package com.dev.photoshare.service.FollowService;

import com.dev.photoshare.dto.response.FollowResponse;
import com.dev.photoshare.entity.Follows;
import com.dev.photoshare.entity.UserStats;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.exception.ResourceNotFoundException;
import com.dev.photoshare.repository.FollowRepository;
import com.dev.photoshare.repository.UserRepository;
import com.dev.photoshare.repository.UserStatsRepository;
import com.dev.photoshare.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService implements IFollowService{
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final UserStatsRepository userStatsRepository;

    @Transactional
    public FollowResponse toggleFollow(int userId) {
        Users target = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Integer followerId = ((CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getId();

        Follows follow = followRepository.findByFollowerIdAndFollowedId(followerId, target.getId())
                .orElse(null);

        boolean followedNow;
        if (follow == null) {
            Follows newFollow = new Follows();
            newFollow.setFollowed(target);
            newFollow.setFollower(new Users(followerId));
            followRepository.save(newFollow);
            updateUserStats(followerId, target.getId(), true);
            followedNow = true;
        } else {
            followRepository.delete(follow);
            updateUserStats(followerId, target.getId(), false);
            followedNow = false;
        }

        UserStats followerStats = userStatsRepository.findByUserId(followerId)
                .orElse(new UserStats());
        UserStats targetStats = userStatsRepository.findByUserId(target.getId())
                .orElse(new UserStats());

        return FollowResponse.builder()
                .status(followedNow ? "followed" : "unfollowed")
                .followerFollowingCount(followerStats.getFollowingCount())
                .followerFollowersCount(followerStats.getFollowersCount())
                .targetFollowingCount(targetStats.getFollowingCount())
                .targetFollowersCount(targetStats.getFollowersCount())
                .build();
    }

    private void updateUserStats(Integer followerId, Integer targetId, boolean increment) {
        if (increment) {
            if (userStatsRepository.incrementFollowingCount(followerId) == 0) {
                Users follower = userRepository.findById(followerId)
                        .orElseThrow(() -> new ResourceNotFoundException("User", "userId", followerId));
                UserStats stats = new UserStats();
                stats.setUser(follower);
                stats.setFollowingCount(1);
                userStatsRepository.save(stats);
            }
            if (userStatsRepository.incrementFollowersCount(targetId) == 0) {
                Users target = userRepository.findById(targetId)
                        .orElseThrow(() -> new ResourceNotFoundException("User", "userId", followerId));
                UserStats stats = new UserStats();
                stats.setUser(target);
                stats.setFollowersCount(1);
                userStatsRepository.save(stats);
            }
        } else {
            userStatsRepository.decrementFollowingCount(followerId);
            userStatsRepository.decrementFollowersCount(targetId);
        }
    }
}
