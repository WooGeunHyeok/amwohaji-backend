package com.amwohaji.backend.domain.community.service;

import com.amwohaji.backend.domain.community.dto.*;
import com.amwohaji.backend.domain.community.entity.CommunityPost;
import com.amwohaji.backend.domain.community.repository.CommunityPostRepository;
import com.amwohaji.backend.global.exception.CustomException;
import com.amwohaji.backend.global.exception.ErrorCode;
import com.amwohaji.backend.global.file.dto.AttachmentResponseDto;
import com.amwohaji.backend.global.file.entity.AttachmentReferenceType;
import com.amwohaji.backend.global.file.service.FileStorageService;
import com.amwohaji.backend.global.like.entity.Like;
import com.amwohaji.backend.global.like.entity.LikeReferenceType;
import com.amwohaji.backend.global.like.repository.LikeRepository;
import com.amwohaji.backend.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityPostService {

    private static final int MAX_LIMIT = 100;
    private static final long COMMUNITY_ATTACHMENT_MAX_FILE_SIZE = 20L * 1024 * 1024;

    private final CommunityPostRepository communityPostRepository;
    private final FileStorageService fileStorageService;
    private final LikeRepository likeRepository;

    /**
     * 게시물 등록
     */
    @Transactional
    public Long createPost(Long userId, CommunityPostCreateRequestDto requestDto) {
        return createPost(userId, requestDto, List.of());
    }

    /**
     * 게시물 등록 (첨부파일 포함)
     */
    @Transactional
    public Long createPost(Long userId, CommunityPostCreateRequestDto requestDto, List<MultipartFile> files) {

        boolean isUserValid = communityPostRepository.existsActiveUserByUserId(userId) > 0;
        if(!isUserValid){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        CommunityPost postEntity = CommunityPost.builder()
                .userId(userId)
                .areaCd(requestDto.getAreaCd())
                .signguCd(requestDto.getSignguCd())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .viewCount(0)
                .isDeleted("N")
                .build();

        CommunityPost savePost = communityPostRepository.save(postEntity);
        fileStorageService.storeAll(
                files,
                AttachmentReferenceType.COMMUNITY,
                savePost.getPostId(),
                COMMUNITY_ATTACHMENT_MAX_FILE_SIZE
        );

        return savePost.getPostId();
    }

    /**
     * 게시물 삭제
     */
    @Transactional
    public void deletePost(Long userId, Long postId) {
        // 삭제할 게시물이 존재하는지 확인
        CommunityPost post =  communityPostRepository.findByPostIdAndUserIdAndIsDeleted(postId, userId, "N")
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        CommunityPost deletePost = CommunityPost.builder()
                .postId(post.getPostId())
                .userId(post.getUserId())
                .areaCd(post.getAreaCd())
                .signguCd(post.getSignguCd())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .insDate(post.getInsDate())
                .isDeleted("Y")
                .updDate(LocalDateTime.now())
                .build();

        communityPostRepository.save(deletePost);
        fileStorageService.deleteAllByReference(AttachmentReferenceType.COMMUNITY, postId);
    }

    /**
     * 게시물 수정
     */
    @Transactional
    public Long updatePost(Long userId, Long postId, CommunityPostUpdateRequestDto requestDto) {
        return updatePost(userId, postId, requestDto, List.of());
    }

    /**
     * 게시물 수정 (첨부파일 포함)
     */
    @Transactional
    public Long updatePost(Long userId, Long postId, CommunityPostUpdateRequestDto requestDto, List<MultipartFile> files) {

        // User ACTIVE 상태 검증
        boolean isUserValid = communityPostRepository.existsActiveUserByUserId(userId) > 0;
        if(!isUserValid){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // 수정할 게시물 존재 여부 확인
        CommunityPost post =  communityPostRepository.findByPostIdAndIsDeleted(postId, "N")
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 작성자 본인 확인
        if (!post.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        post.updatePost(
                requestDto.getAreaCd(),
                requestDto.getSignguCd(),
                requestDto.getTitle(),
                requestDto.getContent()
        );

        // 삭제할 첨부파일이 지정된 경우에만 해당 첨부파일 삭제
        if (requestDto.getDeleteAttachmentIds() != null && !requestDto.getDeleteAttachmentIds().isEmpty()) {
            fileStorageService.deleteAllByIds(requestDto.getDeleteAttachmentIds(), AttachmentReferenceType.COMMUNITY, postId);
        }

        if (hasUploadFiles(files)) {
            fileStorageService.storeAll(
                    files,
                    AttachmentReferenceType.COMMUNITY,
                    postId,
                    COMMUNITY_ATTACHMENT_MAX_FILE_SIZE
            );
        }

        return post.getPostId();
    }

    /**
     * 게시물 목록 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<CommunityPostListResponseDto> getPostList(String areaCd, List<String> signguCds, int limit, int page) {
        if (signguCds != null && signguCds.isEmpty()) {
            signguCds = null;
        }

        int safeLimit = normalizeLimit(limit);
        int safePage = normalizePage(page);
        int offset = calculateOffset(safeLimit, safePage);

        List<CommunityPostListResponseDto> items = communityPostRepository.findPostListWithThumbnail(areaCd, signguCds, safeLimit, offset);
        long totalCount = communityPostRepository.countByAreaCdAndSignguCdInAndIsDeleted(areaCd, signguCds, "N");

        return PageResponse.of(items, safeLimit, safePage, totalCount);
    }

    /**
     * 게시물 상세 조회
     */
    @Transactional(readOnly = true)
    public CommunityPostDetailResponseDto getPostDetail(Long userId, Long postId) {
        // 게시물 조회수 +1 증가
        communityPostRepository.incrementViewCount(postId);

        // 1. 게시물 + USER 닉네임 정보 조회
        Map<String, Object> postMap = communityPostRepository.findPostDetailWithUser(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 삭제된 게시물입니다. id=" + postId));

        // 2. 첨부파일 URL 목록 조회
        List<AttachmentResponseDto> attachments = communityPostRepository.findAttachmentsByReference(AttachmentReferenceType.COMMUNITY, postId);

        // 본인 작성 여부 확인
        boolean isOwner = userId.equals(((Number) postMap.get("userId")).longValue());

        return CommunityPostDetailResponseDto.builder()
                .postId(((Number) postMap.get("postId")).longValue())
                .nickname((String) postMap.get("nickname"))
                .areaCd((String) postMap.get("areaCd"))
                .signguCd((String) postMap.get("signguCd"))
                .title((String) postMap.get("title"))
                .content((String) postMap.get("content"))
                .viewCount(((Number) postMap.get("viewCount")).longValue())
                .insDate((LocalDateTime) postMap.get("insDate"))
                .updDate((LocalDateTime) postMap.get("updDate"))
                .attachments(attachments)
                .isOwner(isOwner)
                .build();
    }

    /**
     * 게시물 좋아요 등록
     */
    @Transactional
    public Long likePost(Long userId, Long postId) {
        communityPostRepository.findByPostIdAndIsDeleted(postId, "N")
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (likeRepository.existsByUserIdAndReferenceTypeAndReferenceId(userId, LikeReferenceType.COMMUNITY_POST, postId)) {
            throw new CustomException(ErrorCode.DUPLICATE_LIKE);
        }

        Like likePost = likeRepository.save(Like.create(userId, LikeReferenceType.COMMUNITY_POST, postId));
        return likePost.getLikeId();
    }

    /**
     * 게시물 좋아요 취소
     */
    @Transactional
    public void cancelLikePost(Long userId, Long postId) {
        communityPostRepository.findByPostIdAndIsDeleted(postId, "N")
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!likeRepository.existsByUserIdAndReferenceTypeAndReferenceId(userId, LikeReferenceType.COMMUNITY_POST, postId)) {
            throw new CustomException(ErrorCode.LIKE_NOT_FOUND);
        }

        likeRepository.deleteByUserIdAndReferenceTypeAndReferenceId(userId, LikeReferenceType.COMMUNITY_POST, postId);
    }

    private boolean hasUploadFiles(List<MultipartFile> files) {
        return files != null && files.stream().anyMatch(file -> file != null && !file.isEmpty());
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
