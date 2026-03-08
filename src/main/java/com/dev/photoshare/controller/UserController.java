package com.dev.photoshare.controller;

import com.dev.photoshare.dto.request.EditProfileRequest;
import com.dev.photoshare.dto.request.UserSearchRequest;
import com.dev.photoshare.dto.response.*;
import com.dev.photoshare.service.ProfileService.ProfileService;
import com.dev.photoshare.service.UserService.IUserService;
import com.dev.photoshare.service.UserService.UserService;
import com.dev.photoshare.utils.ResponseEntityBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "User APIs")
public class UserController {
    private final IUserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<LstProfileResponse>>> getAllUsers(
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "pageNum phải >= 1")
            int pageNum,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "pageSize phải >= 1")
            @Max(value = 100, message = "pageSize tối đa là 100") int pageSize, UserSearchRequest req) {
        PageResponse<LstProfileResponse> response = userService.lstProfile(pageNum, pageSize, req);
        return ResponseEntityBuilder.ok(String.format("Tìm thấy %d user", response.getTotalElements()),  response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<String>> updateUserStatus(
            @PathVariable Integer id,
            @RequestParam("status") Integer status) {

        String saveStatus = userService.updateUserStatus(id, status);
        return ResponseEntityBuilder.ok("Trạng thái đã được thay đổi", saveStatus);
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(@PathVariable Integer userId,
                                                              @RequestParam("roleName")  String roleName) {
        UserResponse savedUser = userService.updateUserRole(userId,  roleName);
        return ResponseEntityBuilder.ok("Vai trò người dùng đã được thay đổi", savedUser);
    }
}


