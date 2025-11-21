package com.dev.photoshare.service.UserService;

import com.dev.photoshare.dto.response.LstProfileResponse;
import com.dev.photoshare.dto.response.PageData;
import com.dev.photoshare.entity.Profiles;
import com.dev.photoshare.entity.UserStats;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.repository.UserRepository;
import com.dev.photoshare.utils.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService{
    private final UserRepository userRepository;

    @Override
    public PageData<LstProfileResponse> lstProfile(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        Page<Users> page = userRepository.findAllWithStats(pageable);

        List<LstProfileResponse> dtoList = page.getContent().stream()
                .map(user -> {
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
                })
                .toList();

        return PageData.<LstProfileResponse>builder()
                .contents(dtoList)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .build();
    }

    @Transactional
    public boolean updateUserStatus(Integer userId, Integer status) {
        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;
        UserStatus newStatus;
        if (status == 1) newStatus = UserStatus.ACTIVE;
        else if (status == 0) newStatus = UserStatus.BANNED;
        else return false;

        user.setStatus(newStatus);
        userRepository.save(user);
        return true;
    }
}
