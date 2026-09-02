package com.amwohaji.backend.domain.community.controller;

import com.amwohaji.backend.domain.community.dto.CommunityCommentCreateRequestDto;
import com.amwohaji.backend.domain.community.dto.CommunityCommentDetailResponseDto;
import com.amwohaji.backend.domain.community.dto.CommunityCommentUpdateRequestDto;
import com.amwohaji.backend.domain.community.service.CommunityCommentService;
import com.amwohaji.backend.global.jwt.UserPrincipal;
import com.amwohaji.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/community/comments")
@RequiredArgsConstructor
public class CommunityCommentController {

    private final CommunityCommentService communityCommentService;

    /**
     * [API] 게시물 댓글/대댓글 등록
     * POST http://localhost:8080/api/v1/community/comments
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Long>> CreateComment(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CommunityCommentCreateRequestDto requestDto) {

        Long commentId = communityCommentService.createComment(principal.getUserId(), requestDto);
        return ResponseEntity.ok(ApiResponse.ok("게시물 댓글 작성이 완료되었습니다.", commentId));
    }

    /**
     * [API] 게시물 댓글/대댓글 등록 (첨부파일 포함)
     * POST http://localhost:8080/api/v1/community/comments
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Long>> CreateCommentWithFiles(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestPart("request") CommunityCommentCreateRequestDto requestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        Long commentId = communityCommentService.createComment(principal.getUserId(), requestDto, files);
        return ResponseEntity.ok(ApiResponse.ok("게시물 댓글 작성이 완료되었습니다.", commentId));
    }

    /**
     * [API] 게시물 댓글/대댓글 삭제
     * POST http://localhost:8080/api/v1/community/comments/delete/{commentId}
     */
    @PostMapping("/delete/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("commentId") Long commentId) {

        communityCommentService.deleteComment(principal.getUserId(), commentId);
        return ResponseEntity.ok(ApiResponse.ok("댓글이 성공적으로 삭제되었습니다.", null));
    }

    /**
     * [API] 게시물 댓글 수정
     * POST http://localhost:8080/api/v1/community/comments/update/{commentId}
     */
    @PostMapping(value = "/update/{commentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Long>> updateComment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommunityCommentUpdateRequestDto requestDto) {

        Long updateCommentId = communityCommentService.updateComment(principal.getUserId(), commentId, requestDto);
        return ResponseEntity.ok(ApiResponse.ok("댓글이 성공적으로 수정되었습니다.", updateCommentId));
    }

    /**
     * [API] 게시물 댓글 수정 (첨부파일 포함)
     * POST http://localhost:8080/api/v1/community/comments/update/{commentId}
     */
    @PostMapping(value = "/update/{commentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Long>> updateCommentWithFiles(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("commentId") Long commentId,
            @RequestPart("request") CommunityCommentUpdateRequestDto requestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        Long updateCommentId = communityCommentService.updateComment(principal.getUserId(), commentId, requestDto, files);
        return ResponseEntity.ok(ApiResponse.ok("댓글이 성공적으로 수정되었습니다.", updateCommentId));
    }

    /**
     * [API] 게시물 댓글 조회
     * GET http://localhost:8080/api/v1/community/comments/{postId}
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<List<CommunityCommentDetailResponseDto>>> getCommentList(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("postId") Long postId) {

        List<CommunityCommentDetailResponseDto> responseDto = communityCommentService.getCommentDetail(principal.getUserId(), postId);
        return ResponseEntity.ok(ApiResponse.ok("게시물 댓글 조회가 완료되었습니다.", responseDto));
    }

    /**
     * [API] 게시물 댓글 좋아요 등록
     * POST http://localhost:8080/api/v1/community/comments/{commentId}/likes
     */
    @PostMapping("/{commentId}/likes")
    public ResponseEntity<ApiResponse<Long>> likeComment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("commentId") Long commentId) {

        Long likeCommentId = communityCommentService.likeComment(principal.getUserId(), commentId);
        return ResponseEntity.ok(ApiResponse.ok("게시물 댓글 좋아요 등록 완료되었습니다.", likeCommentId));
    }

    /**
     * [API] 게시물 댓글 좋아요 취소
     * POST http://localhost:8080/api/v1/community/comments/{commentId}/likes/cancel
     */
    @PostMapping("/{commentId}/likes/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelLikeComment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("commentId") Long commentId) {

        communityCommentService.cancelLikeComment(principal.getUserId(), commentId);
        return ResponseEntity.ok(ApiResponse.ok("게시물 댓글 좋아요 취소 완료되었습니다.", null));
    }
}
