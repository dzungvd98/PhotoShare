package com.dev.photoshare.service.UserService;

import com.dev.photoshare.dto.response.LstProfileResponse;
import com.dev.photoshare.dto.response.PageData;

public interface IUserService {
    PageData<LstProfileResponse> lstProfile(int pageNumber, int pageSize);

    boolean updateUserStatus(Integer userId, Integer status);
}
