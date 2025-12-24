package com.dev.photoshare.service.ProfileService;

import com.dev.photoshare.dto.request.EditProfileRequest;
import com.dev.photoshare.dto.response.*;

public interface IProfileService {
    ProfileResponse getUserProfileProfile(int userId);
    PageResponse<PhotoResponse> getListPhotoPostedOfProfile(int userId, int pageNumber, int pageSize);
    PageResponse<PhotoResponse>  getListPhotoLikedOfProfile(int userId, int pageNumber, int pageSize);
    EditProfileResponse editProfile(int userId, EditProfileRequest editProfileRequest);


}
