package com.dev.photoshare.service.PhotoService;

import com.dev.photoshare.dto.projection.PhotoFeedView;
import com.dev.photoshare.dto.request.PhotoUpdateRequest;
import com.dev.photoshare.dto.request.PhotoUploadRequest;
import com.dev.photoshare.dto.response.*;
import com.dev.photoshare.entity.*;
import com.dev.photoshare.exception.BusinessException;
import com.dev.photoshare.exception.ResourceNotFoundException;
import com.dev.photoshare.repository.*;
import com.dev.photoshare.service.UserStatsService.UserStatsService;
import com.dev.photoshare.utils.enums.ModerationStatus;
import com.dev.photoshare.utils.enums.PhotoStatus;
import com.dev.photoshare.utils.enums.UserStatus;
import jakarta.persistence.EntityNotFoundException;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoService implements IPhotoService {
    private static final String UPLOAD_DIR = "upload/images/";
    private final PhotoRepository photoRepository;
    private final UserStatsService userStatsService;
    private final TagRepository tagRepository;

    @Transactional
    public long uploadPhoto(PhotoUploadRequest req, MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) return 0;

        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Photos photo = new Photos();
        photo.setDescription(req.getDescription());
        photo.setUrl("/uploads/" + fileName);
        photo.setFileSize(image.getSize());
        photo.setStatus(PhotoStatus.PENDING);
        photo.setModerationStatus(ModerationStatus.PENDING);
        photo.setIsArchived(false);

        convertAndSavePhotoTag(req.getTags(), photo);

        Photos saved = photoRepository.save(photo);
        log.info("Photo saved with id {}", saved.getId());

        return saved.getId();
    }

    @Transactional
    public PhotoResponse updatePhoto(int userId, long photoId, PhotoUpdateRequest request) {
        Photos photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo", "id", photoId));

        photo.validateEditableBy(userId);

        photo.setTitle(request.getTitle());
        photo.setDescription(request.getDescription());

        if(photo.getModerationStatus() == ModerationStatus.APPROVED) {
            Users creator = photo.getUser();
            userStatsService.decreasePostCount(creator);
        }


        photo.setModerationStatus(ModerationStatus.PENDING);

        if (request.getTags() != null) {
            updatePhotoTags(photo, request.getTags());
        }

        return PhotoResponse.builder()
                .photoId(photo.getId())
                .photoUrl(photo.getUrl())
                .description(photo.getDescription())
                .title(photo.getTitle())
                .build();
    }

    @Transactional
    public void deletePhoto(long photoId, int userId) {
        Photos photo = getAndValidatePhoto(photoId);
        photo.validateEditableBy(userId);

        if(photo.getModerationStatus() == ModerationStatus.APPROVED) {
            Users creator = photo.getUser();
            userStatsService.decreasePostCount(creator);
        }

        photo.setIsArchived(true);
    }

    @Override
    public PhotoDetailResponse getPhotoDetail(long photoId) {
        Photos photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo", "id", photoId));

        Users creator = photo.getUser();

        String creatorName = StringUtils.hasText(creator.getProfile().getDisplayName())
                ? creator.getProfile().getDisplayName()
                : creator.getUsername();

        PhotoStats stats = photo.getStats();

        List<Tags> tags = tagRepository.findAllByPhotoId(photoId);

        List<String> listTagsResponse = tags.stream()
                .map(Tags::getTagName)
                .toList();

        return PhotoDetailResponse.builder()
                .photoUrl(photo.getUrl())
                .description(photo.getDescription())
                .tags(listTagsResponse)
                .creatorName(creatorName)
                .likeCount(stats.getLikeCount())
                .commentCount(stats.getCommentCount())
                .ownerId(creator.getId())
                .ownerAvatar(creator.getProfile().getAvatarUrl())
                .build();
    }

    @Transactional
    public PhotoReviewResponse reviewPhoto(
            long photoId,
            int modId,
            ModerationStatus targetStatus,
            String reason
    ) {

        Photos photo = getAndValidatePhoto(photoId);

        if (photo.getModerationStatus() != ModerationStatus.PENDING) {
            throw new BusinessException("Ảnh đã được kiểm duyệt trước đó");
        }

        ModerationStatus oldStatus = photo.getModerationStatus();

        switch (targetStatus) {
            case APPROVED -> handleApprove(photo, modId);
            case REJECTED -> handleReject(photo, modId, reason);
            default -> throw new BusinessException("Trạng thái ảnh không hợp lệ");
        }

        photoRepository.save(photo);

        return PhotoReviewResponse.builder()
                .photoId(photo.getId())
                .oldStatus(oldStatus)
                .newStatus(targetStatus)
                .moderatedBy(modId)
                .moderatedAt(photo.getModeratedAt())
                .reason(targetStatus == ModerationStatus.REJECTED ? reason : null)
                .build();
    }


    @Override
    public PageResponse<AwaitingApprovalPhotoResponse> getListAwaitingApprovalPhoto(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Photos> photoPage = photoRepository.findPendingPhotosByActiveUsers(ModerationStatus.PENDING, UserStatus.ACTIVE,  pageable);

        Page<AwaitingApprovalPhotoResponse> result = photoPage.map(this::mapToAwaitingApprovalPhoto);

        return PageResponse.from(result);
    }

    @Override
    public PageResponse<PhotoFeedView> getListPopularPhotos(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<PhotoFeedView> popularPhotos = photoRepository.findPopularPhotos(pageable);

        return PageResponse.from(popularPhotos);
    }

    @Override
    public PageResponse<PhotoFeedView> getListFollowPhotos(int userId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<PhotoFeedView> popularPhotos = photoRepository.findFollowedUsersPhotos(userId, pageable);

        return PageResponse.from(popularPhotos);
    }

    @Override
    public PageResponse<PhotoFeedView> getListNewPhotos(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<PhotoFeedView> popularPhotos = photoRepository.findLatestPhotos(pageable);

        return PageResponse.from(popularPhotos);
    }


    private AwaitingApprovalPhotoResponse mapToAwaitingApprovalPhoto(Photos photo) {
        return AwaitingApprovalPhotoResponse.builder()
                .imgUrl(photo.getUrl())
                .uploadDate(photo.getCreatedAt())
                .description(photo.getDescription())
                .title(photo.getTitle())
                .tags(photo.getPhotoTags().stream()
                        .map(pt -> pt.getTags().getTagName())
                        .toList())
                .photoId(photo.getId())
                .ownerId(photo.getUser().getId())
                .creatorAvatar(photo.getUser().getProfile().getAvatarUrl())
                .creatorName(
                        photo.getUser().getProfile().getDisplayName() != null
                                ? photo.getUser().getProfile().getDisplayName()
                                : photo.getUser().getUsername()

                ).build();
    }

    private void convertAndSavePhotoTag(List<String> tags, Photos photo) {
        List<PhotoTags> photoTagsList = new ArrayList<>();
        for (String tagName : tags) {

            Tags tag = tagRepository.findByTagName(tagName)
                    .orElseGet(() -> {
                        Tags t = new Tags();
                        t.setTagName(tagName);
                        return tagRepository.save(t);
                    });

            tag.incrementUsage();

            PhotoTags pt = new PhotoTags();
            pt.setPhoto(photo);
            pt.setTags(tag);
            photoTagsList.add(pt);
        }
        photo.setPhotoTags(photoTagsList);
    }

    private void handleApprove(Photos photo, int modId) {
        photo.setStatus(PhotoStatus.APPROVED);
        photo.setModerationStatus(ModerationStatus.APPROVED);

        if (photo.getStats() == null) {
            PhotoStats stats = new PhotoStats();
            stats.setPhoto(photo);
            photo.setStats(stats);
        }

        photo.setIsArchived(true);

        Users creator = photo.getUser();
        userStatsService.increasePostCount(creator);

        photo.setModeratedBy(new Users(modId));
        photo.setModeratedAt(LocalDateTime.now());
    }

    private void handleReject(Photos photo, int modId, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new BusinessException("Reject reason is required");
        }

        photo.setStatus(PhotoStatus.REJECTED);
        photo.setModerationStatus(ModerationStatus.REJECTED);
        photo.setRejectionReason(reason);

        photo.setModeratedBy(new Users(modId));
        photo.setModeratedAt(LocalDateTime.now());
    }

    private void updatePhotoTags(Photos photo, List<String> newTagNames) {
        List<PhotoTags> existingPhotoTags = new ArrayList<>(photo.getPhotoTags());

        // Remove tags not in new list
        existingPhotoTags.removeIf(pt -> {
            boolean shouldRemove = !newTagNames.contains(pt.getTags().getTagName());
            if (shouldRemove) {
                photo.getPhotoTags().remove(pt);
                pt.getTags().decrementUsage();
            }
            return shouldRemove;
        });

        // Add new tags
        Set<String> existingTagNames = existingPhotoTags.stream()
                .map(pt -> pt.getTags().getTagName())
                .collect(Collectors.toSet());

        for (String tagName : newTagNames) {
            if (!existingTagNames.contains(tagName)) {
                Tags tag = tagRepository.findByTagName(tagName)
                        .orElseGet(() -> {
                            Tags t = new Tags();
                            t.setTagName(tagName);
                            return tagRepository.save(t);
                        });

                PhotoTags photoTag = new PhotoTags();
                photoTag.setPhoto(photo);
                photoTag.setTags(tag);

                photo.getPhotoTags().add(photoTag);
                tag.incrementUsage();
            }
        }
    }

    private Photos getAndValidatePhoto(long photoId) {
        Photos photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo", "id", photoId));

        photo.validateUpdatableStatus();

        return photo;
    }
}