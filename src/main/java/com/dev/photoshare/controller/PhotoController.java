package com.dev.photoshare.controller;

import com.dev.photoshare.dto.request.PhotoUploadRequest;
import com.dev.photoshare.dto.response.ApiResponse;
import com.dev.photoshare.dto.response.PhotoDetailResponse;
import com.dev.photoshare.dto.response.PhotoResponse;
import com.dev.photoshare.service.PhotoService.IPhotoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequestMapping("api/photos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Photo Controller")
public class PhotoController {
    private final IPhotoService photoService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> uploadPhoto(
            @RequestPart("data") String photoData,
            @RequestPart(value = "image", required = true) MultipartFile image) {
    try {
        ObjectMapper objectMapper = new ObjectMapper();
        PhotoUploadRequest request = objectMapper.readValue(photoData, PhotoUploadRequest.class);

        long idCreated = photoService.uploadPhoto(request, image);
        ApiResponse<Long> response = ApiResponse.<Long>builder()
                .status(HttpStatus.CREATED.value())
                .message("Photo uploaded successfully")
                .data(idCreated)
                .build();
        URI location = URI.create("/api/photos/" + idCreated);
        return ResponseEntity
                .created(location) // tương đương status 201 + header Location
                .body(response);
    } catch (Exception e) {
        log.error("Error uploading photo: {}", e.getMessage(), e); // ghi log nội bộ

        ApiResponse<Long> response = ApiResponse.<Long>builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Failed to upload photo")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{photoId}")
    public ResponseEntity<PhotoDetailResponse> getPhotoDetail(@PathVariable long photoId) {
        return ResponseEntity.ok(photoService.getPhotoDetail(photoId));
    }
}
