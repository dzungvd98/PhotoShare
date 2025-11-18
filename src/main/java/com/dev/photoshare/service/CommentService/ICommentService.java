package com.dev.photoshare.service.CommentService;

import com.dev.photoshare.dto.request.CommentRequest;
import com.dev.photoshare.dto.response.CommentResponse;

public interface ICommentService {
    CommentResponse addComment(CommentRequest comment, long targetId, int userId);
    Boolean deleteComment(long commentId, int userId);
}
