package com.dev.photoshare.service.PhotoService;

import com.dev.photoshare.dto.projection.PhotoFeedView;
import com.dev.photoshare.dto.request.PhotoUpdateRequest;
import com.dev.photoshare.dto.request.PhotoUploadRequest;
import com.dev.photoshare.dto.response.*;
import com.dev.photoshare.utils.enums.ModerationStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IPhotoService {
    PhotoDetailResponse getPhotoDetail(long photoId, int userId);
    PageResponse<AwaitingApprovalPhotoResponse> getListAwaitingApprovalPhoto(int pageNumber, int pageSize);
    PageResponse<PhotoFeedView> getListPopularPhotos(int pageNumber, int pageSize);
    PageResponse<PhotoFeedView> getListFollowPhotos(int userId, int pageNumber, int pageSize);
    PageResponse<PhotoFeedView> getListNewPhotos(int pageNumber, int pageSize);
    PhotoReviewResponse reviewPhoto(long photoId, int modId, ModerationStatus targetStatus, String reason);
    long uploadPhoto(int userId, PhotoUploadRequest photoUploadRequest,  MultipartFile image) throws IOException;
    PhotoResponse updatePhoto(int userId, long photoId, PhotoUpdateRequest request);
    void deletePhoto(long photoId, int userId);

}
