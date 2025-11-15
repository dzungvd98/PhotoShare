package com.dev.photoshare.service.ProfileService;

import com.dev.photoshare.dto.request.EditProfileRequest;
import com.dev.photoshare.dto.response.EditProfileResponse;
import com.dev.photoshare.dto.response.PageData;
import com.dev.photoshare.dto.response.PhotoResponse;
import com.dev.photoshare.dto.response.ProfileResponse;

public interface IProfileService {
    ProfileResponse getUserProfileProfile(int userId);
    PageData<PhotoResponse> getListPhotoPostedOfProfile(int userId, int pageNumber, int pageSize);
    PageData<PhotoResponse> getListPhotoLikedOfProfile(int userId, int pageNumber, int pageSize);
    EditProfileResponse editProfile(int userId, EditProfileRequest editProfileRequest);
}
