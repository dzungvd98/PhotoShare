package com.dev.photoshare.controller;


import com.dev.photoshare.entity.Follows;
import com.dev.photoshare.service.FollowService.IFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {
    private final IFollowService iFollowService;

    @PostMapping
    public ResponseEntity<?> follow(@RequestBody Follows follows) {
        Integer get =
    }
}

