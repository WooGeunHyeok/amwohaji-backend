package com.amwohaji.backend.domain.mypage.service;

import com.amwohaji.backend.domain.community.dto.CommunityPostListResponseDto;
import com.amwohaji.backend.domain.mypage.repository.MyPageRepository;
import com.amwohaji.backend.global.exception.CustomException;
import com.amwohaji.backend.global.exception.ErrorCode;
import com.amwohaji.backend.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private static final int MAX_LIMIT = 100;

    private final MyPageRepository myPageRepository;

    /**
     * 마이페이지 - 내가 쓴 글 목록 조회
     */
    public PageResponse<CommunityPostListResponseDto> getMyPosts(Long userId, int limit, int page) {
        int safeLimit = normalizeLimit(limit);
        int safePage = normalizePage(page);
        int offset = calculateOffset(safeLimit, safePage);

        List<CommunityPostListResponseDto> items = myPageRepository.findMyPostListWithThumbnail(userId, safeLimit, offset);
        long totalCount = myPageRepository.countMyPosts(userId);

        return PageResponse.of(items, safeLimit, safePage, totalCount);
    }

    /**
     * 마이페이지 - 좋아요 누른 게시물 목록 조회
     */
    public PageResponse<CommunityPostListResponseDto> getMyLikedPosts(Long userId, int limit, int page) {
        int safeLimit = normalizeLimit(limit);
        int safePage = normalizePage(page);
        int offset = calculateOffset(safeLimit, safePage);

        List<CommunityPostListResponseDto> items = myPageRepository.findLikedPostListWithThumbnail(userId, safeLimit, offset);
        long totalCount = myPageRepository.countLikedPostsByUserId(userId);

        return PageResponse.of(items, safeLimit, safePage, totalCount);
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private int normalizePage(int page) {
        if (page <= 0) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        return page;
    }

    private int calculateOffset(int limit, int page) {
        return (page - 1) * limit;
    }
}
