package com.dev.photoshare.service.SessionService;

import com.dev.photoshare.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService implements ISessionService{
    private final SessionRepository sessionRepository;
}
