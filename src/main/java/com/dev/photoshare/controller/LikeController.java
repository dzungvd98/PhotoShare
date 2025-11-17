package com.dev.photoshare.controller;

import com.dev.photoshare.security.CustomUserDetails;
import com.dev.photoshare.service.LikeService.ILikeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/likes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Like Controller")
public class LikeController {
    private final ILikeService likeService;

    @PostMapping("/photos/{photoId}")
    public ResponseEntity<Boolean> toggleLike(@PathVariable long photoId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = userDetails.getId();
        return ResponseEntity.ok(likeService.toggleLike(userId, photoId));
    }
}
