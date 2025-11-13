package com.dev.photoshare.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PageData<T> {
    List<T> contents;
    int pageNumber;
    int pageSize;
    int totalPages;
    long totalElements;
    int numberOfElements;
}
