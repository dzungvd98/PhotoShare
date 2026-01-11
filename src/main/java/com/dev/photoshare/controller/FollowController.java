package com.dev.photoshare.controller;


import com.dev.photoshare.dto.response.ApiResponse;
import com.dev.photoshare.dto.response.FollowResponse;
import com.dev.photoshare.service.FollowService.IFollowService;
import com.dev.photoshare.utils.ResponseEntityBuilder;
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
    public ResponseEntity<ApiResponse<FollowResponse>> toggleFollow(@RequestParam int userId) {
        FollowResponse followResponse = iFollowService.toggleFollow(userId);
        return ResponseEntityBuilder.ok(followResponse);
    }
}

