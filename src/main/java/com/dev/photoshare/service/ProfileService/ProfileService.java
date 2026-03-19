package com.dev.photoshare.service.ProfileService;

import com.dev.photoshare.dto.request.EditProfileRequest;
import com.dev.photoshare.dto.response.*;
import com.dev.photoshare.entity.Photos;
import com.dev.photoshare.entity.Profiles;
import com.dev.photoshare.entity.UserStats;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.exception.ResourceNotFoundException;
import com.dev.photoshare.repository.PhotoRepository;
import com.dev.photoshare.repository.ProfileRepository;
import com.dev.photoshare.repository.UserRepository;
import com.dev.photoshare.service.R2Service.R2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService implements IProfileService{
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final ProfileRepository profileRepository;
    private final R2Service r2Service;

    @Override
    public ProfileResponse getUserProfileProfile(int userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        Profiles profile = user.getProfile();
        UserStats userStats = user.getUserStats();

        String displayName = profile.getDisplayName().trim();

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
    public PageResponse<PhotoResponse> getListPhotoPostedOfProfile(int userId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        Page<Photos> photoPage = photoRepository.findAllByUser_IdOrderByUpdatedAtDesc(userId, pageable);
        Page<PhotoResponse> result = photoPage.map(this::mapToPostedOfProfile);

        return PageResponse.from(result);
    }

    @Override
    public PageResponse<PhotoResponse> getListPhotoLikedOfProfile(int userId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        Page<Photos> photoPage = photoRepository.findPhotosLikeByUser(userId, pageable);
        Page<PhotoResponse> result = photoPage.map(this::mapToPostedOfProfile);

        return PageResponse.from(result);
    }

    @Transactional
    public EditProfileResponse editProfile(int userId, EditProfileRequest editProfileRequest, MultipartFile file ) throws IOException {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        Profiles profile = user.getProfile();

        if(!StringUtils.hasText(editProfileRequest.getDisplayName())) {
            profile.setDisplayName(editProfileRequest.getDisplayName());
        }

        if(!StringUtils.hasText(editProfileRequest.getBio())) {
            profile.setBio(editProfileRequest.getBio());
        }

        String avatarUrl = r2Service.upload(file);
        if(StringUtils.hasText(avatarUrl)) {
            profile.setAvatarUrl(avatarUrl);
        }
        Profiles savedProfile = profileRepository.save(profile);

        return EditProfileResponse.builder()
                .displayName(savedProfile.getDisplayName())
                .bio(savedProfile.getBio())
                .avatarUrl(savedProfile.getAvatarUrl())
                .userId(userId)
                .build();
    }

    private PhotoResponse mapToPostedOfProfile(Photos photo) {
        return PhotoResponse.builder()
                .photoUrl(photo.getUrl())
                .photoId(photo.getId())
                .description(photo.getDescription())
                .build();
    }


}
