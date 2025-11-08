package com.dev.photoshare.service.UserService;

import com.dev.photoshare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService{
    private final UserRepository userRepository;
}
