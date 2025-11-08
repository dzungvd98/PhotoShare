package com.dev.photoshare.service.TagService;

import com.dev.photoshare.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService implements ITagService{
    private final TagRepository tagRepository;
}
