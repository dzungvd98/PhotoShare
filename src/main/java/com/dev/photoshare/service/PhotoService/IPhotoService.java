package com.dev.photoshare.service.PhotoService;

import com.dev.photoshare.dto.request.PhotoUploadRequest;
import com.dev.photoshare.dto.response.PhotoDetailResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IPhotoService {
    long uploadPhoto(PhotoUploadRequest photoUploadRequest,  MultipartFile image) throws IOException;
    PhotoDetailResponse getPhotoDetail(long photoId);
}
