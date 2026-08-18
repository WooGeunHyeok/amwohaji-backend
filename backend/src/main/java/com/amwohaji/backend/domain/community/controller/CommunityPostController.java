package com.amwohaji.backend.domain.community.controller;

import com.amwohaji.backend.domain.community.dto.*;
import com.amwohaji.backend.domain.community.service.CommunityPostService;
import com.amwohaji.backend.global.jwt.UserPrincipal;
import com.amwohaji.backend.global.response.ApiResponse;
import com.amwohaji.backend.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/community/posts")
@RequiredArgsConstructor
public class CommunityPostController {

    private final CommunityPostService communityService;

    /**
     * [API] 게시물 생성
     * POST http://localhost:8080/api/v1/community/posts
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Long>> createPost(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CommunityPostCreateRequestDto request) {

        Long postId  = communityService.createPost(principal.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.ok("게시물이 성공적으로 등록되었습니다.", postId));
    }

    /**
     * [API] 게시물 생성 (첨부파일 포함)
     * POST http://localhost:8080/api/v1/community/posts
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Long>> createPostWithFiles(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestPart("request") CommunityPostCreateRequestDto request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        Long postId = communityService.createPost(principal.getUserId(), request, files);
        return ResponseEntity.ok(ApiResponse.ok("게시물이 성공적으로 등록되었습니다.", postId));
    }

    /**
     * [API] 게시물 삭제
     * POST http://localhost:8080/api/v1/community/posts/delete/{postId}
     */
    @PostMapping("/delete/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("postId") Long postId) {

        communityService.deletePost(principal.getUserId(), postId);
        return ResponseEntity.ok(ApiResponse.ok("게시물이 성공적으로 삭제되었습니다.", null));
    }

    /**
     * [API] 게시물 수정
     * POST http://localhost:8080/api/v1/community/posts/update/{postId}
     */
    @PostMapping(value = "/update/{postId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Long>> updatePost(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("postId") Long postId,
            @RequestBody CommunityPostUpdateRequestDto requestDto) {

        Long updatePostId = communityService.updatePost(principal.getUserId(), postId, requestDto);
        return ResponseEntity.ok(ApiResponse.ok("게시물이 성공적으로 수정되었습니다.", updatePostId));
    }

    /**
     * [API] 게시물 수정 (첨부파일 포함)
     * POST http://localhost:8080/api/v1/community/posts/update/{postId}
     */
    @PostMapping(value = "/update/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Long>> updatePostWithFiles(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("postId") Long postId,
            @RequestPart("request") CommunityPostUpdateRequestDto requestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        Long updatePostId = communityService.updatePost(principal.getUserId(), postId, requestDto, files);
        return ResponseEntity.ok(ApiResponse.ok("게시물이 성공적으로 수정되었습니다.", updatePostId));
    }

    /**
     * [API] 게시물 목록 조회
     * GET http://localhost:8080/api/v1/community/posts?areaCd=11&signguCd=11010
     */
    @GetMapping()
    public ResponseEntity<ApiResponse<PageResponse<CommunityPostListResponseDto>>> getPostList(
            @RequestParam(name = "areaCd", required = false) String areaCd,
            @RequestParam(name = "signguCd", required = false) List<String> signguCds,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestParam(name = "page", defaultValue = "1") int page) {

        return ResponseEntity.ok(ApiResponse.ok("게시물 목록 조회가 완료되었습니다.", communityService.getPostList(areaCd, signguCds, limit, page)));
    }

    /**
     * [API] 게시물 상세 조회
     * GET http://localhost:8080/api/v1/community/posts/{postId}
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<CommunityPostDetailResponseDto>> getPostDetail(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("postId") Long postId) {

        CommunityPostDetailResponseDto responseDto = communityService.getPostDetail(principal.getUserId(), postId);
        return ResponseEntity.ok(ApiResponse.ok("게시물 상제 조회가 완료되었습니다.", responseDto));
    }
}
