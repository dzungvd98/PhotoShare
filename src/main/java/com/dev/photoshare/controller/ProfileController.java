package com.dev.photoshare.controller;

import com.dev.photoshare.dto.request.EditProfileRequest;
import com.dev.photoshare.dto.response.*;
import com.dev.photoshare.service.ProfileService.ProfileService;
import com.dev.photoshare.utils.ResponseEntityBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<PhotoResponse> pageResponse = profileService.getListPhotoPostedOfProfile(userId, pageNum, pageSize);
        return ResponseEntityBuilder.ok(String.format("Tìm thấy %d ảnh", pageResponse.getTotalElements()), pageResponse);
    }

    @GetMapping("/users/{userId}/liked")
    public ResponseEntity<ApiResponse<PageResponse<PhotoResponse>>> getListLikedPhotosOfProfile(
            @PathVariable int userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<PhotoResponse> pageResponse = profileService.getListPhotoLikedOfProfile(userId, pageNum, pageSize);
        return ResponseEntityBuilder.ok(String.format("Tìm thấy %d ảnh", pageResponse.getTotalElements()), pageResponse);
    }

    @PostMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<EditProfileResponse>> editProfile(
            @PathVariable int userId,
            @RequestBody EditProfileRequest editProfileRequest
    ) {
        return ResponseEntityBuilder.ok("Cập nhật thành công", profileService.editProfile(userId, editProfileRequest));
    }

}
