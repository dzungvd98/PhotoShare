package com.dev.photoshare.service.RoleService;

import com.dev.photoshare.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService implements IRoleService{
    private final RoleRepository roleRepository;
}

