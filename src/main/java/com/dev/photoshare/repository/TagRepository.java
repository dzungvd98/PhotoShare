package com.dev.photoshare.repository;

import com.dev.photoshare.entity.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tags,Long> {

    @Query("SELECT t FROM Tags t JOIN t.photoTags pt WHERE pt.photo.id = :photoId")
    List<Tags> findAllByPhotoId(@Param("photoId") Long photoId);
}
