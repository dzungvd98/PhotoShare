package com.dev.photoshare.service.CommentService;

import com.dev.photoshare.dto.request.CommentRequest;
import com.dev.photoshare.dto.response.CommentResponse;
import com.dev.photoshare.entity.CommentStats;
import com.dev.photoshare.entity.Comments;
import com.dev.photoshare.entity.Photos;
import com.dev.photoshare.repository.CommentRepository;
import com.dev.photoshare.repository.PhotoRepository;
import com.dev.photoshare.utils.enums.CommentType;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService{
    private final CommentRepository commentRepository;
    private final PhotoRepository photoRepository;

    @Transactional
    public CommentResponse addComment(CommentRequest comment, long targetId, int userId) {
        Comments cmt = new Comments();
        if(comment.getCommentType().equals(CommentType.PHOTO)) {
            Photos photos = photoRepository.findById(targetId).orElseThrow(
                    () -> new EntityNotFoundException("Photos not found with id: " + targetId)
            );
            cmt.setPhoto(photos);

            CommentStats stats = new CommentStats();
            stats.setComment(cmt);
            cmt.setStats(stats);
            commentRepository.save(cmt);
        } else if(comment.getCommentType().equals(CommentType.REPLY)) {
            Comments existing = commentRepository.findById(targetId).orElseThrow(
                    () -> new EntityNotFoundException("Comments not found with id: " + targetId)
            );

            existing.getStats().increaseReplyCount();
            commentRepository.save(existing);

            cmt.setParent(existing);
            commentRepository.save(cmt);
        }

        cmt.setContent(comment.getContent());
        cmt.setStatus("ACTIVE");

        return CommentResponse.builder()
                .content(comment.getContent())
                .createdDate(LocalDateTime.now())
                .targetId(targetId)
                .status(comment.getCommentType().equals(CommentType.PHOTO) ? "Commented" : "Replied")
                .build();

    }

    @Transactional
    public Boolean deleteComment(long commentId, int userId) {
        Comments existing = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Comments not found with id: " + commentId)
        );

        if(existing.getUser().getId() != userId) {
            throw new ValidationException("User not allowed to delete comment!");
        }

        if(existing.getParent() != null) {
            Comments parent = existing.getParent();
            if (parent.getStats() != null) {
                parent.getStats().decreaseReplyCount();
            }
        }

        try {
            commentRepository.delete(existing);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
