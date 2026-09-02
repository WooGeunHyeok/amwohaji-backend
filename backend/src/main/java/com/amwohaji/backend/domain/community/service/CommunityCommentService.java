package com.amwohaji.backend.domain.community.service;

import com.amwohaji.backend.domain.community.dto.CommunityCommentCreateRequestDto;
import com.amwohaji.backend.domain.community.dto.CommunityCommentDetailResponseDto;
import com.amwohaji.backend.domain.community.dto.CommunityCommentUpdateRequestDto;
import com.amwohaji.backend.domain.community.entity.CommunityComment;
import com.amwohaji.backend.domain.community.repository.CommunityCommentRepository;
import com.amwohaji.backend.domain.community.repository.CommunityPostRepository;
import com.amwohaji.backend.global.exception.CustomException;
import com.amwohaji.backend.global.exception.ErrorCode;
import com.amwohaji.backend.global.file.dto.AttachmentResponseDto;
import com.amwohaji.backend.global.file.entity.AttachmentReferenceType;
import com.amwohaji.backend.global.file.service.FileStorageService;
import com.amwohaji.backend.global.like.entity.Like;
import com.amwohaji.backend.global.like.entity.LikeReferenceType;
import com.amwohaji.backend.global.like.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityCommentService {

    private static final long COMMENT_ATTACHMENT_MAX_FILE_SIZE = 20L * 1024 * 1024;

    private final CommunityPostRepository communityPostRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final LikeRepository likeRepository;
    private final FileStorageService fileStorageService;

    /**
     * 게시물 댓글 등록
     */
    @Transactional
    public Long createComment (Long userId, CommunityCommentCreateRequestDto requestDto) {
        return createComment(userId, requestDto, List.of());
    }

    /**
     * 게시물 댓글 등록 (첨부파일 포함)
     */
    @Transactional
    public Long createComment (Long userId, CommunityCommentCreateRequestDto requestDto, List<MultipartFile> files) {
        communityPostRepository.findByPostIdAndIsDeleted(requestDto.getPostId(), "N")
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 데이터 저장 실행
        CommunityComment commentEntity = CommunityComment.builder()
                .postId(requestDto.getPostId())
                .userId(userId)
                .parentId(requestDto.getParentId())
                .content(requestDto.getContent())
                .isDeleted("N")
                .build();

        CommunityComment saveComment = communityCommentRepository.save(commentEntity);
        fileStorageService.storeAll(
                files,
                AttachmentReferenceType.COMMENT,
                saveComment.getCommentId(),
                COMMENT_ATTACHMENT_MAX_FILE_SIZE
        );

        return saveComment.getCommentId();
    }

    /**
     * 게시물 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        CommunityComment comment = communityCommentRepository.findByCommentIdAndUserIdAndIsDeleted(commentId, userId, "N")
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 내 댓글 삭제
        communityCommentRepository.deleteComment(commentId);
        fileStorageService.deleteAllByReference(AttachmentReferenceType.COMMENT, commentId);
    }

    /**
     * 게시물 댓글 수정
     */
    @Transactional
    public Long updateComment(Long userId, Long commentId, CommunityCommentUpdateRequestDto requestDto) {
        return updateComment(userId, commentId, requestDto, List.of());
    }

    /**
     * 게시물 댓글 수정 (첨부파일 포함)
     */
    @Transactional
    public Long updateComment(Long userId, Long commentId, CommunityCommentUpdateRequestDto requestDto, List<MultipartFile> files) {

        // 수정할 댓글 존재
        CommunityComment comment = communityCommentRepository.findByCommentIdAndIsDeleted(commentId, "N")
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 작성자 본인 확인
        if (!comment.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        comment.updateComment(
                requestDto.getContent()
        );

        // 삭제할 첨부파일이 지정된 경우에만 해당 첨부파일 삭제
        if (requestDto.getDeleteAttachmentIds() != null && !requestDto.getDeleteAttachmentIds().isEmpty()) {
            fileStorageService.deleteAllByIds(requestDto.getDeleteAttachmentIds(), AttachmentReferenceType.COMMENT, commentId);
        }

        if (hasUploadFiles(files)) {
            fileStorageService.storeAll(
                    files,
                    AttachmentReferenceType.COMMENT,
                    commentId,
                    COMMENT_ATTACHMENT_MAX_FILE_SIZE
            );
        }

        return comment.getCommentId();
    }

    /**
     * 게시물 댓글 조회
     */
    @Transactional
    public List<CommunityCommentDetailResponseDto> getCommentDetail(Long userId, Long postId) {
        // 1. Native Query로 Map 리스트 받아오기 (Repository에서 데이터 가져오기 부모 댓글, 자식 댓글 구분 없이 섞여서 들어옴.)
        List<Map<String, Object>> commentMaps = communityCommentRepository.findCommentByPostId(postId);

        // dtoMap : 자식 댓글이 부모 댓글을 빠르게 찾기위한 변수
        Map<Long, CommunityCommentDetailResponseDto> dtoMap = new HashMap<>();
        // rootComment : 최종적으로 변환 할 리스트 (responseDto)
        List<CommunityCommentDetailResponseDto> rootComment = new ArrayList<>();

        // 2. Map 데이터 순회하면서 DTO 변환 및 트리 조립
        for (Map<String, Object> map : commentMaps) {
            Long commentId = ((Number) map.get("commentId")).longValue();
            Long parentId = map.get("parentId") != null ? ((Number) map.get("parentId")).longValue() : null;

            boolean isOwner = userId.equals(((Number) map.get("userId")).longValue());

            CommunityCommentDetailResponseDto responseDto = CommunityCommentDetailResponseDto.builder()
                    .commentId(commentId)
                    .nickname((String) map.get("nickname"))
                    .parentId(parentId)
                    .content((String) map.get("content"))
                    .insDate((LocalDateTime) map.get("insDate"))
                    .updDate((LocalDateTime) map.get("updDate"))
                    .attachments(findCommentAttachments(commentId))
                    .isOwner(isOwner)
                    .children(new ArrayList<>())
                    .build();

            // 빠른 조회를 위해 Map에 저장
            dtoMap.put(commentId, responseDto);

            if (parentId == null) {
                // 부모 댓글
                rootComment.add(responseDto);
            } else {
                // 대댓글(자식)인 경우
                CommunityCommentDetailResponseDto parentComment = dtoMap.get(parentId);
                if (parentComment != null) {
                    parentComment.getChildren().add(responseDto);
                }
            }
        }
        return rootComment;
    }

    /**
     * 게시물 댓글 좋아요 등록
     */
    @Transactional
    public long likeComment(Long userId, Long commentId) {
        communityCommentRepository.findByCommentIdAndIsDeleted(commentId, "N")
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (likeRepository.existsByUserIdAndReferenceTypeAndReferenceId(userId, LikeReferenceType.COMMUNITY_COMMENT, commentId)) {
            throw new CustomException(ErrorCode.DUPLICATE_LIKE);
        }

        Like likeComment = likeRepository.save(Like.create(userId, LikeReferenceType.COMMUNITY_COMMENT, commentId));
        return likeComment.getLikeId();
    }

    /**
     * 게시물 댓글 좋아요 취소
     */
    @Transactional
    public void cancelLikeComment(Long userId, Long commentId) {
        communityCommentRepository.findByCommentIdAndIsDeleted(commentId, "N")
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!likeRepository.existsByUserIdAndReferenceTypeAndReferenceId(userId, LikeReferenceType.COMMUNITY_COMMENT, commentId)) {
            throw new CustomException(ErrorCode.LIKE_NOT_FOUND);
        }

        likeRepository.deleteByUserIdAndReferenceTypeAndReferenceId(userId, LikeReferenceType.COMMUNITY_COMMENT, commentId);
    }

    private List<AttachmentResponseDto> findCommentAttachments(Long commentId) {
        return fileStorageService.findByReference(AttachmentReferenceType.COMMENT, commentId).stream()
                .map(attachment -> AttachmentResponseDto.builder()
                        .attachmentId(attachment.getAttachmentId())
                        .fileUrl(attachment.getFileUrl())
                        .build())
                .toList();
    }

    private boolean hasUploadFiles(List<MultipartFile> files) {
        return files != null && files.stream().anyMatch(file -> file != null && !file.isEmpty());
    }
}
