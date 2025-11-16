package com.dev.photoshare.service.PhotoService;

import com.dev.photoshare.dto.request.PhotoUploadRequest;
import com.dev.photoshare.dto.response.PhotoDetailResponse;
import com.dev.photoshare.dto.response.PhotoResponse;
import com.dev.photoshare.entity.PhotoStats;
import com.dev.photoshare.entity.Photos;
import com.dev.photoshare.entity.Tags;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.repository.PhotoRepository;
import com.dev.photoshare.repository.TagRepository;
import com.dev.photoshare.service.UserStatsService.UserStatsService;
import com.dev.photoshare.utils.enums.ModerationStatus;
import com.dev.photoshare.utils.enums.PhotoStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        PhotoStats stats = new PhotoStats();
        stats.setPhoto(photo);
        photo.setStats(stats);

        Photos saved = photoRepository.save(photo);
        log.info("Photo saved with id {}", saved.getId());

        Users creator = saved.getUser();
        userStatsService.increasePostCount(creator);

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
}