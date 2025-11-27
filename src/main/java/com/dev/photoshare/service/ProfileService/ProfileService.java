package com.dev.photoshare.service.ProfileService;

import com.dev.photoshare.dto.request.EditProfileRequest;
import com.dev.photoshare.dto.response.*;
import com.dev.photoshare.entity.Photos;
import com.dev.photoshare.entity.Profiles;
import com.dev.photoshare.entity.UserStats;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.exception.UserNotFoundException;
import com.dev.photoshare.repository.PhotoRepository;
import com.dev.photoshare.repository.ProfileRepository;
import com.dev.photoshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService implements IProfileService{
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final ProfileRepository profileRepository;

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
                        .build()).toList();

        return PageData.<PhotoResponse>builder()
                .contents(photoResponses)
                .pageNumber(photoPage.getNumber() + 1)
                .pageSize(photoPage.getSize())
                .totalPages(photoPage.getTotalPages())
                .totalElements(photoPage.getTotalElements())
                .build();
    }

    @Override
    public PageData<PhotoResponse> getListPhotoLikedOfProfile(int userId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        Page<Photos> photoPage = photoRepository.findPhotosLikeByUser(userId, pageable);
        List<PhotoResponse> photoResponses = photoPage.getContent().stream()
                .map(photo -> PhotoResponse.builder()
                        .photoUrl(photo.getUrl())
                        .photoId(photo.getId())
                        .description(photo.getDescription())
                        .build()).toList();

        return PageData.<PhotoResponse>builder()
                .contents(photoResponses)
                .pageNumber(photoPage.getNumber() + 1)
                .pageSize(photoPage.getSize())
                .totalPages(photoPage.getTotalPages())
                .totalElements(photoPage.getTotalElements())
                .build();
    }

    @Transactional
    public EditProfileResponse editProfile(int userId, EditProfileRequest editProfileRequest) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Profiles profile = user.getProfile();

        if(!StringUtils.hasText(editProfileRequest.getDisplayName())) {
            profile.setDisplayName(editProfileRequest.getDisplayName());
        }

        if(!StringUtils.hasText(editProfileRequest.getAvatarUrl())) {
            profile.setAvatarUrl(editProfileRequest.getAvatarUrl());
        }

        if(!StringUtils.hasText(editProfileRequest.getBio())) {
            profile.setBio(editProfileRequest.getBio());
        }

        Profiles savedProfile = profileRepository.save(profile);

        return EditProfileResponse.builder()
                .displayName(savedProfile.getDisplayName())
                .bio(savedProfile.getBio())
                .avatarUrl(savedProfile.getAvatarUrl())
                .userId(userId)
                .build();
    }


}
