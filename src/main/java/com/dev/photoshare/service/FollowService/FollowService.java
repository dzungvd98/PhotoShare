package com.dev.photoshare.service.FollowService;

import com.dev.photoshare.dto.response.FollowResponse;
import com.dev.photoshare.entity.Follows;
import com.dev.photoshare.entity.UserStats;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.exception.UserNotFoundException;
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
    public String followUser(String targetUsername) {
        Integer followerId = ((CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getId();

        Users target = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new UserNotFoundException("Target user not found"));

        Follows follow = followRepository.findByFollowerIdAndFollowedId(followerId, target.getId())
                .orElse(new Follows());

        boolean isNewFollow = follow.getId() == null;

        follow.setFollowed(target);

        followRepository.save(follow);

        int rows = userStatsRepository.incrementFollowingCount(followerId);
        if (rows == 0) {
            UserStats stats = new UserStats();
            stats.setUserId(followerId);
            stats.setFollowingCount(1);
            userStatsRepository.save(stats);
        }

        // followed
        rows = userStatsRepository.incrementFollowersCount(target.getId());
        if (rows == 0) {
            UserStats stats = new UserStats();
            stats.setUserId(target.getId());
            stats.setFollowersCount(1);
            userStatsRepository.save(stats);
        }

        return isNewFollow ? "Follow request sent" : "Follow updated";
    }


    @Transactional
    public String unfollowUser(String targetUsername) {
        Integer followerId = ((CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getId();

        Users target = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new UserNotFoundException("Target user not found"));

        Follows follow = followRepository.findByFollowerIdAndFollowedId(followerId, target.getId())
                .orElseThrow(() -> new NoSuchElementException("Follow relationship not found"));

        followRepository.delete(follow);

        userStatsRepository.decrementFollowingCount(followerId);
        userStatsRepository.decrementFollowersCount(target.getId());

        return "Unfollowed successfully";
    }

    @Transactional
    public FollowResponse toggleFollow(String targetUsername) {
        Users target = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new UserNotFoundException("Target user not found"));

        Integer followerId = ((CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getId();

        Follows follow = followRepository.findByFollowerIdAndFollowedId(followerId, target.getId())
                .orElse(null);

        boolean followedNow;
        if (follow == null) {
            Follows newFollow = new Follows();
            newFollow.setFollowed(target);
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
                        .orElseThrow(() -> new UserNotFoundException("User not found"));
                UserStats stats = new UserStats();
                stats.setUser(follower);
                stats.setFollowingCount(1);
                userStatsRepository.save(stats);
            }
            if (userStatsRepository.incrementFollowersCount(targetId) == 0) {
                Users target = userRepository.findById(targetId)
                        .orElseThrow(() -> new UserNotFoundException("User not found"));
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
