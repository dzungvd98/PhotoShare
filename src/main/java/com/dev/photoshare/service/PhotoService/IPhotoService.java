package com.dev.photoshare.service.PhotoService;

import com.dev.photoshare.dto.request.PhotoUploadRequest;
import com.dev.photoshare.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IPhotoService {
    long uploadPhoto(PhotoUploadRequest photoUploadRequest,  MultipartFile image) throws IOException;
    PhotoDetailResponse getPhotoDetail(long photoId);
    PhotoReviewResponse approvePhoto(long photoId, int modId);
    PhotoReviewResponse rejectPhoto(long photoId, int modId, String reason);
    PageResponse<AwaitingApprovalPhotoResponse> getListAwaitingApprovalPhoto(int pageNumber, int pageSize);
}
