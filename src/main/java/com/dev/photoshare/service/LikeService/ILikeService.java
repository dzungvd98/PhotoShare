package com.dev.photoshare.service.LikeService;

public interface ILikeService {
    boolean toggleLike(int userId, long photoId);
}
