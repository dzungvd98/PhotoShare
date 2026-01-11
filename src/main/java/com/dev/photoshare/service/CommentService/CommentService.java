package com.dev.photoshare.service.CommentService;

import com.dev.photoshare.dto.projection.CommentProjection;
import com.dev.photoshare.dto.request.CommentRequest;
import com.dev.photoshare.dto.response.CommentResponse;
import com.dev.photoshare.dto.response.PageResponse;
import com.dev.photoshare.entity.Comments;
import com.dev.photoshare.entity.Photos;
import com.dev.photoshare.entity.Profiles;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.exception.BusinessException;
import com.dev.photoshare.exception.ResourceNotFoundException;
import com.dev.photoshare.repository.CommentRepository;
import com.dev.photoshare.repository.PhotoRepository;
import com.dev.photoshare.repository.UserRepository;
import com.dev.photoshare.utils.enums.CommentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService{
    private final CommentRepository commentRepository;
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse addComment(CommentRequest comment, long targetId, int userId) {
        Comments cmt = new Comments();
        cmt.setContent(comment.getContent());
        cmt.setUser(new Users(userId));

        if(comment.getCommentType().equals(CommentType.PHOTO)) {
            Photos photos = photoRepository.findById(targetId).orElseThrow(
                    () -> new ResourceNotFoundException("Photo", "id",  targetId)
            );
            cmt.setPhoto(photos);

        } else if(comment.getCommentType().equals(CommentType.REPLY)) {
            Comments existing = commentRepository.findById(targetId).orElseThrow(
                    () -> new ResourceNotFoundException("Comments", "id", targetId)
            );

            cmt.setParent(existing);
            cmt.setPhoto(existing.getPhoto());

            existing.getReplies().add(cmt);
            existing.increaseReplyCount();

            commentRepository.save(existing);
        }

        commentRepository.save(cmt);

        Users user = userRepository.getReferenceById(userId);

        cmt.setContent(comment.getContent());
        cmt.setUser(user);
        Comments saved = commentRepository.save(cmt);
        return CommentResponse.builder()
                .commentContent(comment.getContent())
                .createdDate(saved.getCreatedAt())
                .userId(userId)
                .userAvatar(saved.getUser().getProfile().getAvatarUrl())
                .userDisplayName(
                        Optional.ofNullable(saved.getUser().getProfile())
                                .map(Profiles::getDisplayName)
                                .filter(name -> !name.isBlank())
                                .orElse(null)
                )
                .commentId(saved.getId())
                .status(comment.getCommentType().equals(CommentType.PHOTO) ? "Commented" : "Replied")
                .build();
    }

    @Override
    public PageResponse<CommentProjection> getMainComments(Long photoId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommentProjection> result = commentRepository.findRootCommentsByPhotoId(photoId, pageable);
        return PageResponse.from(result);
    }

    @Override
    public PageResponse<CommentProjection> getReplies(Long parentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommentProjection> result = commentRepository.findRepliesByParentId(parentId, pageable);
        return PageResponse.from(result);
    }

    @Override
    public CommentResponse updateComment(Long commentId, String content, int userId) {
        Comments existing = commentRepository.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundException("Comment", "Id", commentId)
        );

        if (existing.isDeleted()) throw new BusinessException("This comment has been deleted");

        if(!existing.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to update this comment");
        }

        existing.setContent(content);
        Comments saved = commentRepository.save(existing);
        return CommentResponse.builder()
                .commentContent(content)
                .createdDate(saved.getCreatedAt())
                .userId(userId)
                .userAvatar(saved.getUser().getProfile().getAvatarUrl())
                .userDisplayName(
                        Optional.ofNullable(saved.getUser().getProfile())
                                .map(Profiles::getDisplayName)
                                .filter(name -> !name.isBlank())
                                .orElse(null)
                )
                .commentId(saved.getId())
                .status("Updated")
                .build();
    }

    @Transactional
    public void deleteComment(long commentId, int userId, Collection<? extends GrantedAuthority> authorities) {
        Comments existing = commentRepository.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundException("Comment", "Id", commentId)
        );

        boolean isPrivileged = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.equals("ROLE_ADMIN") || r.equals("ROLE_MOD"));

        if (existing.isDeleted()) throw new BusinessException("This comment has been deleted");

        if (!isPrivileged && existing.getUser().getId() != userId) {
            throw new AccessDeniedException("Not allowed");
        }


        if (existing.getParent() == null) {
            commentRepository.softDeleteRootAndReplies(existing.getId());
        } else {
            existing.getParent().decreaseReplyCount();
            existing.setDeleted(true);
        }
    }
}
