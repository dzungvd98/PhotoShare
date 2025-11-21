package com.dev.photoshare.controller;

import com.dev.photoshare.dto.request.EditProfileRequest;
import com.dev.photoshare.dto.response.*;
import com.dev.photoshare.service.ProfileService.ProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/profiles")
@RequiredArgsConstructor
@Tag(name = "Profile Controller", description = "Profile APIs")
public class ProfileController {
    private final ProfileService profileService;





    @GetMapping("/users/{userId}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable int userId) {
        ProfileResponse profileResponse = profileService.getUserProfileProfile(userId);
        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<PageData<PhotoResponse>> getListPostsOfProfile(
            @PathVariable int userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(profileService.getListPhotoPostedOfProfile(userId, pageNum, pageSize));
    }

    @GetMapping("/users/{userId}/liked")
    public ResponseEntity<PageData<PhotoResponse>> getListLikedPhotosOfProfile(
            @PathVariable int userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(profileService.getListPhotoLikedOfProfile(userId, pageNum, pageSize));
    }

    @PostMapping("/users/{userId}")
    public ResponseEntity<EditProfileResponse> editProfile(
            @PathVariable int userId,
            @RequestBody EditProfileRequest editProfileRequest
    ) {
        return ResponseEntity.ok(profileService.editProfile(userId, editProfileRequest));
    }


}
