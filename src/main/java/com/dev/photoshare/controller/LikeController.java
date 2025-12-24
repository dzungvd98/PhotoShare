package com.dev.photoshare.controller;

import com.dev.photoshare.dto.response.ApiResponse;
import com.dev.photoshare.security.CustomUserDetails;
import com.dev.photoshare.service.LikeService.ILikeService;
import com.dev.photoshare.utils.ResponseEntityBuilder;
import com.dev.photoshare.utils.enums.LikeableType;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/likes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Like Controller")
public class LikeController {
    private final ILikeService likeService;

    @PostMapping
    public ResponseEntity<ApiResponse<Boolean>> toggleLike(@RequestParam long targetId,
                                                          @RequestParam LikeableType type) {
        int userId = getUserIdFromToken();
        return ResponseEntityBuilder.ok(likeService.toggleLike(userId, targetId, type));
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
