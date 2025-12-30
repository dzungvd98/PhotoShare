package com.dev.photoshare.service.UserService;

import com.dev.photoshare.dto.response.LstProfileResponse;
import com.dev.photoshare.dto.response.PageData;
import com.dev.photoshare.dto.response.PageResponse;
import com.dev.photoshare.dto.response.UserResponse;

public interface IUserService {
    PageResponse<LstProfileResponse> lstProfile(int pageNumber, int pageSize);

    String updateUserStatus(Integer userId, Integer status);

    UserResponse updateUserRole(Integer userId, String roleName);
}
