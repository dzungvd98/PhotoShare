package com.dev.photoshare.controller;

import com.dev.photoshare.dto.request.CommentRequest;
import com.dev.photoshare.dto.response.CommentResponse;
import com.dev.photoshare.security.CustomUserDetails;
import com.dev.photoshare.service.CommentService.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable long targetId,
            @RequestBody CommentRequest request) {
        int userId = getUserIdFromToken();
        return ResponseEntity.ok(commentService.addComment(request, targetId, userId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Boolean> deleteComment(
            @PathVariable long commentId) {
        int userId = getUserIdFromToken();
        return ResponseEntity.ok(commentService.deleteComment(commentId, userId));
    }


    private int getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }


}
