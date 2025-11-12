package com.dev.photoshare.service.PhotoService;

import com.dev.photoshare.dto.request.PhotoUploadRequest;
import com.dev.photoshare.entity.PhotoStats;
import com.dev.photoshare.entity.Photos;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.repository.PhotoRepository;
import com.dev.photoshare.service.PhotoFirstViewService.IPhotoFirstViewService;
import com.dev.photoshare.service.UserStatsService.UserStatsService;
import com.dev.photoshare.utils.enums.ModerationStatus;
import com.dev.photoshare.utils.enums.PhotoStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoService implements IPhotoService {
    private static final String UPLOAD_DIR = "upload/images/";
    private final PhotoRepository photoRepository;
    private final UserStatsService userStatsService;

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

        Photos saved = photoRepository.save(photo);
        log.info("Photo saved with id {}", saved.getId());

        Users creator = saved.getUser();
        userStatsService.increasePostCount(creator);

        return saved.getId();
    }
}