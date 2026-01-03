package com.dev.photoshare.controller;

import com.dev.photoshare.dto.projection.CommentProjection;
import com.dev.photoshare.dto.projection.PhotoFeedView;
import com.dev.photoshare.dto.request.CommentRequest;
import com.dev.photoshare.dto.request.PhotoReviewRequest;
import com.dev.photoshare.dto.request.PhotoUpdateRequest;
import com.dev.photoshare.dto.request.PhotoUploadRequest;
import com.dev.photoshare.dto.response.*;
import com.dev.photoshare.security.CustomUserDetails;
import com.dev.photoshare.service.CommentService.ICommentService;
import com.dev.photoshare.service.PhotoService.IPhotoService;
import com.dev.photoshare.utils.ResponseEntityBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/photos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Photo Controller")
public class PhotoController {
    private final IPhotoService photoService;
    private final ICommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> uploadPhoto(
            @RequestPart("data") String photoData,
            @RequestPart(value = "image", required = true) MultipartFile image) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        PhotoUploadRequest request = objectMapper.readValue(photoData, PhotoUploadRequest.class);
        int userId = getUserIdFromToken();

        long idCreated = photoService.uploadPhoto(userId, request, image);
        return ResponseEntityBuilder.created("Tạo ảnh thành công", idCreated);

    }

    @PutMapping("/{photoId}")
    public ResponseEntity<ApiResponse<PhotoResponse>> updatePhoto(
            @PathVariable long photoId,
            @RequestBody @Valid PhotoUpdateRequest request) {
        int userId = getUserIdFromToken();
        PhotoResponse result = photoService.updatePhoto(userId, photoId, request);
        return ResponseEntityBuilder.ok("Ảnh đã được cập nhật, chờ quản trị viên phê duyệt", result);
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> deletePhoto(@PathVariable long photoId) {
        int userId = getUserIdFromToken();
        photoService.deletePhoto(photoId, userId);
        return ResponseEntityBuilder.noContent();
    }

    @GetMapping("/{photoId}")
    public ResponseEntity<ApiResponse<PhotoDetailResponse>> getPhotoDetail(@PathVariable long photoId) {
       return ResponseEntityBuilder.ok(photoService.getPhotoDetail(photoId));
    }

    @PatchMapping("/{photoId}/review")
    public ResponseEntity<ApiResponse<PhotoReviewResponse>> review(@PathVariable long photoId,
                                                           @RequestBody @Valid PhotoReviewRequest request) {

        int modId = getUserIdFromToken();
        return ResponseEntityBuilder.ok("Phê duyệt ảnh thành công",photoService.reviewPhoto(photoId, modId, request.getModerationStatus(), request.getReason()));
    }

    @GetMapping("/pending-approval")
    public ResponseEntity<ApiResponse<PageResponse<AwaitingApprovalPhotoResponse>>> getPendingApprovalPhoto(
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "pageNum phải >= 1")
            int pageNum,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "pageSize phải >= 1")
            @Max(value = 100, message = "pageSize tối đa là 100") int pageSize) {
        PageResponse<AwaitingApprovalPhotoResponse> pageResponse = photoService.getListAwaitingApprovalPhoto(pageNum - 1,  pageSize);
        return ResponseEntityBuilder.ok(String.format("Tìm thấy %d ảnh", pageResponse.getTotalElements()), pageResponse);
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<PageResponse<PhotoFeedView>>> getPopularPhotos(@RequestParam(defaultValue = "1")
                                                                                         @Min(value = 1, message = "pageNum phải >= 1")
                                                                                         int pageNum,
                                                                                     @RequestParam(defaultValue = "10")
                                                                                         @Min(value = 1, message = "pageSize phải >= 1")
                                                                                         @Max(value = 100, message = "pageSize tối đa là 100") int pageSize) {
        PageResponse<PhotoFeedView> pageResponse = photoService.getListPopularPhotos(pageNum - 1,  pageSize);
        return ResponseEntityBuilder.ok(String.format("Tìm thấy %d ảnh", pageResponse.getTotalElements()), pageResponse);
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<PageResponse<PhotoFeedView>>> getLatestPhotos(@RequestParam(defaultValue = "1")
                                                                                        @Min(value = 1, message = "pageNum phải >= 1")
                                                                                        int pageNum,
                                                                                    @RequestParam(defaultValue = "10")
                                                                                        @Min(value = 1, message = "pageSize phải >= 1")
                                                                                        @Max(value = 100, message = "pageSize tối đa là 100") int pageSize) {
        PageResponse<PhotoFeedView> pageResponse = photoService.getListNewPhotos(pageNum - 1,  pageSize);
        return ResponseEntityBuilder.ok(String.format("Tìm thấy %d ảnh", pageResponse.getTotalElements()), pageResponse);
    }

    @GetMapping("/follow")
    public ResponseEntity<ApiResponse<PageResponse<PhotoFeedView>>> getFollowPhotos(
                                                    @RequestParam(defaultValue = "1")
                                                    @Min(value = 1, message = "pageNum phải >= 1")
                                                    int pageNum,
                                                    @RequestParam(defaultValue = "10")
                                                    @Min(value = 1, message = "pageSize phải >= 1")
                                                    @Max(value = 100, message = "pageSize tối đa là 100") int pageSize) {
        int userId = getUserIdFromToken();
        PageResponse<PhotoFeedView> pageResponse = photoService.getListFollowPhotos(userId, pageNum - 1, pageSize);
        return ResponseEntityBuilder.ok(String.format("Tìm thấy %d ảnh", pageResponse.getTotalElements()), pageResponse);
    }

    @GetMapping("/{photoId}/comments")
    public ResponseEntity<ApiResponse<PageResponse<CommentProjection>>> getComments(
            @PathVariable long photoId,
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "pageNum phải >= 1")
            int pageNum,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "pageSize phải >= 1")
            @Max(value = 100, message = "pageSize tối đa là 100") int pageSize) {
        PageResponse<CommentProjection> reponse = commentService.getMainComments(photoId, pageNum - 1, pageSize);
        return ResponseEntityBuilder.ok(String.format("Tìm thấy %d bình luận", reponse.getTotalElements()), reponse);
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
