package com.amwohaji.backend.global.response;

import java.util.List;

public record PageResponse<T>(
        List<T> items,
        int limit,
        int currentPage,
        int totalPages,
        long totalCount,
        int count,
        boolean hasNext
) {

    public static <T> PageResponse<T> of(List<T> items, int limit, int currentPage, long totalCount) {
        int totalPages = limit > 0 ? (int) Math.ceil((double) totalCount / limit) : 0;
        long fetchedCount = (long) (currentPage - 1) * limit + items.size();
        boolean hasNext = fetchedCount < totalCount;

        return new PageResponse<>(
                items,
                limit,
                currentPage,
                totalPages,
                totalCount,
                items.size(),
                hasNext
        );
    }
}
