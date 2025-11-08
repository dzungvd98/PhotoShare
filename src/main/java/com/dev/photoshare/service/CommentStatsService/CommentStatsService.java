package com.dev.photoshare.service.CommentStatsService;

import com.dev.photoshare.repository.CommentStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
@Service
@RequiredArgsConstructor
public class CommentStatsService implements ICommentStatsService{
    private final CommentStatsRepository commentStatsRepository;
}
