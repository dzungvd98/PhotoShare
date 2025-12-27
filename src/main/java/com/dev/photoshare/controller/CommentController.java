package com.dev.photoshare.controller;

import com.dev.photoshare.dto.request.CommentRequest;
import com.dev.photoshare.dto.response.ApiResponse;
import com.dev.photoshare.dto.response.CommentResponse;
import com.dev.photoshare.security.CustomUserDetails;
import com.dev.photoshare.service.CommentService.CommentService;
import com.dev.photoshare.utils.ResponseEntityBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/comments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Comment Controller")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{targetId}")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable long targetId,
            @RequestBody CommentRequest request) {
        int userId = getUserIdFromToken();
        return ResponseEntityBuilder.ok("Bình luận đã được đăng tải", commentService.addComment(request, targetId, userId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable long commentId) {
        int userId = getUserIdFromToken();
        commentService.deleteComment(commentId, userId);
        return ResponseEntityBuilder.noContent();
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
