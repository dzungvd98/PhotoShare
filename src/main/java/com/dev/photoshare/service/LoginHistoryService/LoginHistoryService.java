package com.dev.photoshare.service.LoginHistoryService;

import com.dev.photoshare.repository.LoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginHistoryService implements ILoginHistoryService {
    private final LoginHistoryRepository loginHistoryRepository;
}
