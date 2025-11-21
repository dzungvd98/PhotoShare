package com.dev.photoshare.controller;

import com.dev.photoshare.dto.request.EditProfileRequest;
import com.dev.photoshare.dto.response.*;
import com.dev.photoshare.service.ProfileService.ProfileService;
import com.dev.photoshare.service.UserService.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
@Tag(name = "Profile Controller", description = "Profile APIs")
public class UserController {
    private final UserService userService;


    @GetMapping("")
    public ResponseEntity<PageData<LstProfileResponse>> getAllUsers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageData<LstProfileResponse> response = userService.lstProfile(pageNum, pageSize);
        return ResponseEntity.ok(response);

    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateUserStatus(
            @PathVariable Integer id,
            @RequestParam("status") Integer status) {

        boolean updated = userService.updateUserStatus(id, status);
        if (updated) {
            return ResponseEntity.ok("User status updated successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid status or user not found");
        }
    }




}


