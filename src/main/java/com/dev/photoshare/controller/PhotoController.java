package com.dev.photoshare.controller;

import com.dev.photoshare.dto.request.PhotoRejectRequest;
import com.dev.photoshare.dto.request.PhotoUploadRequest;
import com.dev.photoshare.dto.response.*;
import com.dev.photoshare.security.CustomUserDetails;
import com.dev.photoshare.service.PhotoService.IPhotoService;
import com.dev.photoshare.utils.ResponseEntityBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            @RequestPart(value = "image", required = true) MultipartFile image) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        PhotoUploadRequest request = objectMapper.readValue(photoData, PhotoUploadRequest.class);

        long idCreated = photoService.uploadPhoto(request, image);
        return ResponseEntityBuilder.created("Tạo ảnh thành công", idCreated);

    }

    @GetMapping("/{photoId}")
    public ResponseEntity<ApiResponse<PhotoDetailResponse>> getPhotoDetail(@PathVariable long photoId) {
       return ResponseEntityBuilder.ok(photoService.getPhotoDetail(photoId));
    }

    @PostMapping("/{photoId}/approve")
    public ResponseEntity<ApiResponse<PhotoReviewResponse>> approvePhoto(@PathVariable long photoId) {
        int modId = getUserIdFromToken();
        return ResponseEntityBuilder.ok("Ảnh đã được phê duyệt",photoService.approvePhoto(photoId, modId));
    }

    @PostMapping("/{photoId}/reject")
    public ResponseEntity<ApiResponse<PhotoReviewResponse>> rejectPhoto(@PathVariable long photoId,
                                                           @RequestBody PhotoRejectRequest request) {

        int modId = getUserIdFromToken();
        return ResponseEntityBuilder.ok("Ảnh không được phê duyệt",photoService.rejectPhoto(photoId, modId, request.getReason()));
    }

    @GetMapping("/pending-approval")
    public ResponseEntity<ApiResponse<PageResponse<AwaitingApprovalPhotoResponse>>> getPendingApprovalPhoto(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<AwaitingApprovalPhotoResponse> pageResponse = photoService.getListAwaitingApprovalPhoto(pageNum,  pageSize);
        return ResponseEntityBuilder.ok(String.format("Tìm thấy %d ảnh", pageResponse.getTotalElements()), pageResponse);
    }

    private int getUserIdFromToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new AccessDeniedException("Không có quyền truy cập");
        }

        return userDetails.getId();
    }

}
