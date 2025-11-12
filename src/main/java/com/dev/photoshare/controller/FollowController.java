package com.dev.photoshare.controller;


import com.dev.photoshare.dto.response.FollowResponse;
import com.dev.photoshare.service.FollowService.IFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {
    private final IFollowService iFollowService;

    @PostMapping
    public ResponseEntity<FollowResponse> toggleFollow(@RequestParam String targetUsername) {
        FollowResponse followResponse = iFollowService.toggleFollow(targetUsername);
        return new ResponseEntity<>(followResponse, HttpStatus.OK);
    }
}

