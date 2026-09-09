package com.swetha.graphexplorer.dto.response;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Stable, explicit pagination envelope. Wrapping Spring's {@link Page}
 * instead of returning it directly keeps the API contract independent of
 * Spring Data's internal JSON shape.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last) {

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
