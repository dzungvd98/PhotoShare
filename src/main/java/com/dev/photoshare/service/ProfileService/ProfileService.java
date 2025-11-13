package com.dev.photoshare.service.ProfileService;

import com.dev.photoshare.dto.response.PageData;
import com.dev.photoshare.dto.response.PhotoResponse;
import com.dev.photoshare.dto.response.ProfileResponse;
import com.dev.photoshare.entity.Photos;
import com.dev.photoshare.entity.Profiles;
import com.dev.photoshare.entity.UserStats;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.exception.UserNotFoundException;
import com.dev.photoshare.repository.PhotoRepository;
import com.dev.photoshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService implements IProfileService{
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;

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

    @Override
    public PageData<PhotoResponse> getListPhotoPostedOfProfile(int userId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        Page<Photos> photoPage = photoRepository.findAllByUser_IdOrderByUpdatedAtDesc(userId, pageable);
        List<PhotoResponse> photoResponses = photoPage.getContent().stream()
                .map(photo -> PhotoResponse.builder()
                        .photoUrl(photo.getUrl())
                        .photoId(photo.getId())
                        .description(photo.getDescription())
                        .build()).collect(Collectors.toList());

        return PageData.<PhotoResponse>builder()
                .contents(photoResponses)
                .pageNumber(photoPage.getNumber() + 1)
                .pageSize(photoPage.getSize())
                .totalPages(photoPage.getTotalPages())
                .totalElements(photoPage.getTotalElements())
                .build();
    }
}
