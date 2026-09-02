package com.amwohaji.backend.domain.mypage.controller;

import com.amwohaji.backend.domain.community.dto.CommunityPostListResponseDto;
import com.amwohaji.backend.domain.mypage.service.MyPageService;
import com.amwohaji.backend.global.jwt.UserPrincipal;
import com.amwohaji.backend.global.response.ApiResponse;
import com.amwohaji.backend.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    /**
     * [API] 내가 쓴 글 목록 조회
     * GET http://localhost:8080/api/v1/mypage/posts
     */
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<PageResponse<CommunityPostListResponseDto>>> getMyPosts(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestParam(name = "page", defaultValue = "1") int page) {

        return ResponseEntity.ok(ApiResponse.ok("내가 쓴 글 목록 조회가 완료되었습니다.",
                myPageService.getMyPosts(principal.getUserId(), limit, page)));
    }

    /**
     * [API] 좋아요 누른 게시물 목록 조회
     * GET http://localhost:8080/api/v1/mypage/posts/liked
     */
    @GetMapping("/posts/liked")
    public ResponseEntity<ApiResponse<PageResponse<CommunityPostListResponseDto>>> getMyLikedPosts(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestParam(name = "page", defaultValue = "1") int page) {

        return ResponseEntity.ok(ApiResponse.ok("좋아요 누른 게시물 목록 조회가 완료되었습니다.",
                myPageService.getMyLikedPosts(principal.getUserId(), limit, page)));
    }
}
