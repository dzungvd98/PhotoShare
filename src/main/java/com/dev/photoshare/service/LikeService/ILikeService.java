package com.dev.photoshare.service.LikeService;

import com.dev.photoshare.utils.enums.LikeableType;

public interface ILikeService {
    boolean toggleLikePhoto(int userId, long likeableId, LikeableType likeableType);
}
