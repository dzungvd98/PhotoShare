package com.dev.photoshare.service.FollowService;

import com.dev.photoshare.entity.Follows;
import com.dev.photoshare.entity.UserStats;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.exception.UserNotFoundException;
import com.dev.photoshare.repository.FollowRepository;
import com.dev.photoshare.repository.UserRepository;
import com.dev.photoshare.repository.UserStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public String followUser(Integer followerId, String targetUsername) {
        Users follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserNotFoundException("Follower not found"));

        Users target = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new UserNotFoundException("Target user not found"));

        Follows follow = followRepository.findByFollowerIdAndFollowedId(followerId, target.getId())
                .orElse(new Follows());

        boolean isNewFollow = follow.getId() == null;

        follow.setFollower(follower);
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
    public String unfollowUser(Integer followerId, String targetUsername) {
        Users target = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new UserNotFoundException("Target user not found"));

        Follows follow = followRepository.findByFollowerIdAndFollowedId(followerId, target.getId())
                .orElseThrow(() -> new NoSuchElementException("Follow relationship not found"));

        followRepository.delete(follow);

        userStatsRepository.decrementFollowingCount(followerId);
        userStatsRepository.decrementFollowersCount(target.getId());

        return "Unfollowed successfully";
    }
}
