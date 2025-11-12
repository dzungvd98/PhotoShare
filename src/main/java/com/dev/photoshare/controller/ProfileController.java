package com.dev.photoshare.controller;

import com.dev.photoshare.dto.response.ProfileResponse;
import com.dev.photoshare.service.ProfileService.ProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
