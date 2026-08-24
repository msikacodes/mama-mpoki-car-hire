package com.mamampoki.carhire.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponse<T> {

    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int size;

    public static <T> PaginatedResponse<T> of(List<T> content, long totalElements,
                                                int totalPages, int currentPage, int size) {
        return PaginatedResponse.<T>builder()
                .content(content)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .currentPage(currentPage)
                .size(size)
                .build();
    }
}
