package com.dev.photoshare.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {

    private List<T> content;

    private int page;           // Current page (0-based)
    private int size;           // Items per page
    private long totalElements; // Total number of items
    private int totalPages;     // Total number of pages

    private boolean first;      // Is first page
    private boolean last;       // Is last page
    private boolean empty;      // Is empty page

    private Integer numberOfElements; // Number of items in current page

    /**
     * Convert Spring Data Page to PageResponse
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber() + 1)
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .numberOfElements(page.getNumberOfElements())
                .build();
    }

    /**
     * Convert Spring Data Page với custom mapper
     */
    public static <T, U> PageResponse<U> from(Page<T> page,
                                              java.util.function.Function<T, U> mapper) {
        List<U> mappedContent = page.getContent().stream()
                .map(mapper)
                .toList();

        return PageResponse.<U>builder()
                .content(mappedContent)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .numberOfElements(page.getNumberOfElements())
                .build();
    }
}