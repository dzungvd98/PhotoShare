package com.dev.photoshare.service.PhotoService;

import com.dev.photoshare.dto.request.PhotoUploadRequest;
import com.dev.photoshare.dto.response.AwaitingApprovalPhotoResponse;
import com.dev.photoshare.dto.response.PageData;
import com.dev.photoshare.dto.response.PhotoDetailResponse;
import com.dev.photoshare.dto.response.PhotoReviewResponse;
import com.dev.photoshare.entity.*;
import com.dev.photoshare.repository.PhotoRepository;
import com.dev.photoshare.repository.PhotoTagRepository;
import com.dev.photoshare.repository.TagRepository;
import com.dev.photoshare.service.PhotoStatsService.IPhotoStatsService;
import com.dev.photoshare.service.UserStatsService.UserStatsService;
import com.dev.photoshare.utils.enums.ModerationStatus;
import com.dev.photoshare.utils.enums.PhotoStatus;
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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoService implements IPhotoService {
    private static final String UPLOAD_DIR = "upload/images/";
    private final PhotoRepository photoRepository;
    private final UserStatsService userStatsService;
    private final TagRepository tagRepository;
    private final PhotoTagRepository  photoTagRepository;
    private final IPhotoStatsService  photoStatsService;

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

    @Override
    public PhotoDetailResponse getPhotoDetail(long photoId) {
        Photos photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException("Not exist photo with id " + photoId));

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
    public PhotoReviewResponse approvePhoto(long photoId, int modId) {
        Photos photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException("Not exist photo with id " + photoId));

        ModerationStatus oldStatus = photo.getModerationStatus();
        photo.setStatus(PhotoStatus.APPROVED);
        photo.setModerationStatus(ModerationStatus.APPROVED);

        PhotoStats stats = new PhotoStats();
        stats.setPhoto(photo);
        photo.setStats(stats);
        photo.setIsArchived(true);

        Users creator = photo.getUser();
        userStatsService.increasePostCount(creator);

        photo.setModeratedBy(new Users(modId));
        photo.setModeratedAt(LocalDateTime.now());

        photoRepository.save(photo);

        return PhotoReviewResponse.builder()
                .oldStatus(oldStatus)
                .newStatus(ModerationStatus.APPROVED)
                .message("Photo approved successfully!")
                .moderatedAt(LocalDateTime.now())
                .moderatedBy(modId)
                .reason(null)
                .photoId(photo.getId())
                .build();
    }

    @Override
    public PhotoReviewResponse rejectPhoto(long photoId, int modId, String reason) {
        Photos photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new EntityNotFoundException("Not exist photo with id " + photoId));
        ModerationStatus oldStatus = photo.getModerationStatus();
        photo.setStatus(PhotoStatus.REJECTED);
        photo.setModerationStatus(ModerationStatus.REJECTED);

        photo.setModeratedBy(new Users(modId));
        photo.setModeratedAt(LocalDateTime.now());

        photoRepository.save(photo);

        return PhotoReviewResponse.builder()
                .oldStatus(oldStatus)
                .newStatus(ModerationStatus.REJECTED)
                .message("Photo rejected!")
                .moderatedAt(LocalDateTime.now())
                .moderatedBy(modId)
                .reason(reason)
                .photoId(photo.getId())
                .build();
    }

    @Override
    public PageData<AwaitingApprovalPhotoResponse> getListAwaitingApprovalPhoto(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Photos> photoPage = photoRepository.findPhotosByModerationStatusOrderByUpdatedAtAsc(ModerationStatus.PENDING, pageable);

        List<AwaitingApprovalPhotoResponse> photoResponses = photoPage.getContent().stream()
                .map(photo -> AwaitingApprovalPhotoResponse.builder()
                        .imgUrl(photo.getUrl())
                        .uploadDate(photo.getCreatedAt())
                        .tags(photo.getPhotoTags().stream()
                                .map(pt -> pt.getTags().getTagName())
                                .toList())
                        .photoId(photo.getId())
                        .ownerId(photo.getUser().getId())
                        .creatorName(
                                photo.getUser().getProfile().getDisplayName() != null
                                        ? photo.getUser().getProfile().getDisplayName()
                                        : photo.getUser().getUsername()
                        )
                        .build()).toList();

        return PageData.<AwaitingApprovalPhotoResponse>builder()
                .contents(photoResponses)
                .pageNumber(photoPage.getNumber() + 1)
                .pageSize(photoPage.getSize())
                .totalPages(photoPage.getTotalPages())
                .totalElements(photoPage.getTotalElements())
                .build();
    }

    private void convertAndSavePhotoTag(List<String> tags, Photos photo) {
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

            photoTagRepository.save(pt);
        }
    }


}