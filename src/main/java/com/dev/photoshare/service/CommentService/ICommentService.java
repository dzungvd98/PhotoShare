package com.dev.photoshare.service.CommentService;

import com.dev.photoshare.dto.projection.CommentProjection;
import com.dev.photoshare.dto.request.CommentRequest;
import com.dev.photoshare.dto.response.CommentResponse;
import com.dev.photoshare.dto.response.PageResponse;
import com.dev.photoshare.entity.Comments;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public interface ICommentService {
    CommentResponse addComment(CommentRequest comment, long targetId, int userId);
    PageResponse<CommentProjection> getMainComments(Long photoId, int page, int size);
    PageResponse<CommentProjection> getReplies(Long parentId, int page, int size);
    CommentResponse updateComment(Long commentId, String content, int userId);
    void deleteComment(long commentId, int userId, Collection<? extends GrantedAuthority> authorities);

}
