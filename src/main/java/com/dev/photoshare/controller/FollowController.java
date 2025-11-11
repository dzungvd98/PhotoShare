package com.dev.photoshare.controller;


import com.dev.photoshare.service.FollowService.IFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {
    private final IFollowService iFollowService;

}

