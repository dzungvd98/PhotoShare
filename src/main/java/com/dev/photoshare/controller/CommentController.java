package com.dev.photoshare.controller;

import com.dev.photoshare.dto.projection.CommentProjection;
import com.dev.photoshare.dto.request.CommentRequest;
import com.dev.photoshare.dto.response.ApiResponse;
import com.dev.photoshare.dto.response.CommentResponse;
import com.dev.photoshare.dto.response.PageResponse;
import com.dev.photoshare.security.CustomUserDetails;
import com.dev.photoshare.service.CommentService.ICommentService;
import com.dev.photoshare.utils.ResponseEntityBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/comments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Comment Controller")
public class CommentController {
    private final ICommentService commentService;

    @GetMapping("/{id}/replies")
    public ResponseEntity<ApiResponse<PageResponse<CommentProjection>>> getReplies(
            @PathVariable long id,
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "pageNum phải >= 1")
            int pageNum,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "pageSize phải >= 1")
            @Max(value = 100, message = "pageSize tối đa là 100") int pageSize) {
        PageResponse<CommentProjection> response =  commentService.getReplies(id, pageNum - 1, pageSize);
        return ResponseEntityBuilder.ok(String.format("Tìm thấy %d lượt trả lời bình luận ", response.getTotalElements()), response);
    }

    @PostMapping("/{targetId}/create")
    public ResponseEntity<ApiResponse<CommentResponse>> addComment(
            @PathVariable long targetId,
            @RequestBody CommentRequest request) {
        int userId = getUserIdFromToken();
        return ResponseEntityBuilder.created("Bình luận đã được đăng tải", commentService.addComment(request, targetId, userId));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(@PathVariable long id, @RequestBody CommentRequest commentRequest) {
        int userId = getUserIdFromToken();
        CommentResponse response = commentService.updateComment(id, commentRequest.getContent(), userId);
        return ResponseEntityBuilder.ok("Bình luật đã được cập nhật", response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable long commentId,
            @AuthenticationPrincipal CustomUserDetails user) {

        commentService.deleteComment(commentId, user.getId(), user.getAuthorities());
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
