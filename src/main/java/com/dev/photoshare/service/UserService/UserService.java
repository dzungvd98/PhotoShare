package com.dev.photoshare.service.UserService;

import com.dev.photoshare.dto.response.LstProfileResponse;
import com.dev.photoshare.dto.response.PageData;
import com.dev.photoshare.dto.response.PageResponse;
import com.dev.photoshare.entity.Profiles;
import com.dev.photoshare.entity.UserStats;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.exception.ResourceNotFoundException;
import com.dev.photoshare.repository.UserRepository;
import com.dev.photoshare.utils.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService{
    private final UserRepository userRepository;

    @Override
    public PageResponse<LstProfileResponse> lstProfile(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        Page<Users> page = userRepository.findAllWithStats(pageable);

        Page<LstProfileResponse> result = page.map(this::mapToProfileResponse);

        return PageResponse.from(result);
    }

    @Transactional
    public String updateUserStatus(Integer userId, Integer status) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "UserId", userId));

        UserStatus newStatus = switch (status) {
            case 1 -> UserStatus.ACTIVE;
            case 0 -> UserStatus.BANNED;
            default -> throw new IllegalArgumentException("Invalid status value: " + status);
        };

        user.setStatus(newStatus);
        Users savedUser = userRepository.save(user);
        return savedUser.getStatus().toString();
    }

    private LstProfileResponse mapToProfileResponse(Users user) {
        UserStats stats = user.getUserStats();
        Profiles profile = user.getProfile();
        return LstProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .authProvider(user.getAuthProvider())
                .status(user.getStatus().name())
                .roleName(user.getRole() != null ? user.getRole().getRoleName() : null)
                .postCount(stats != null ? stats.getPostCount() : 0)
                .followingCount(stats != null ? stats.getFollowingCount() : 0)
                .followersCount(stats != null ? stats.getFollowersCount() : 0)
                .displayName(profile != null ? profile.getDisplayName() : null)
                .avatarUrl(profile != null ? profile.getAvatarUrl() : null)
                .build();
    }
}
