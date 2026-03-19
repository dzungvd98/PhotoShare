package com.dev.photoshare.controller;

import com.dev.photoshare.dto.request.EditProfileRequest;
import com.dev.photoshare.dto.request.PhotoUploadRequest;
import com.dev.photoshare.dto.response.*;
import com.dev.photoshare.security.CustomUserDetails;
import com.dev.photoshare.service.ProfileService.ProfileService;
import com.dev.photoshare.utils.ResponseEntityBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("api/profiles")
@RequiredArgsConstructor
@Tag(name = "Profile Controller", description = "Profile APIs")
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(@PathVariable int userId) {
        ProfileResponse profileResponse = profileService.getUserProfileProfile(userId);
        return ResponseEntityBuilder.ok(profileResponse);
    }

    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<ApiResponse<PageResponse<PhotoResponse>>> getListPostsOfProfile(
            @PathVariable int userId,
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "pageNum phải >= 1")
            int pageNum,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "pageSize phải >= 1")
            @Max(value = 100, message = "pageSize tối đa là 100") int pageSize) {
        PageResponse<PhotoResponse> pageResponse = profileService.getListPhotoPostedOfProfile(userId, pageNum, pageSize);
        return ResponseEntityBuilder.ok(String.format("Tìm thấy %d ảnh", pageResponse.getTotalElements()), pageResponse);
    }

    @GetMapping("/users/{userId}/liked")
    public ResponseEntity<ApiResponse<PageResponse<PhotoResponse>>> getListLikedPhotosOfProfile(
            @PathVariable int userId,
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "pageNum phải >= 1")
            int pageNum,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "pageSize phải >= 1")
            @Max(value = 100, message = "pageSize tối đa là 100") int pageSize) {
        PageResponse<PhotoResponse> pageResponse = profileService.getListPhotoLikedOfProfile(userId, pageNum, pageSize);
        return ResponseEntityBuilder.ok(String.format("Tìm thấy %d ảnh", pageResponse.getTotalElements()), pageResponse);
    }

    @PutMapping("/edit")
    public ResponseEntity<ApiResponse<EditProfileResponse>> editProfile(
            @RequestPart("data") String editProfileRequest,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        int userId = getUserIdFromToken();
        ObjectMapper objectMapper = new ObjectMapper();
        EditProfileRequest request = objectMapper.readValue(editProfileRequest, EditProfileRequest.class);
        return ResponseEntityBuilder.ok("Cập nhật thành công", profileService.editProfile(userId, request, file));
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
