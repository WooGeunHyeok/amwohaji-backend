package com.amwohaji.backend.domain.community.repository;

import com.amwohaji.backend.domain.community.dto.CommunityPostListResponseDto;
import com.amwohaji.backend.domain.community.entity.CommunityPost;
import com.amwohaji.backend.global.file.dto.AttachmentResponseDto;
import com.amwohaji.backend.global.file.entity.AttachmentReferenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    // USER ID, 계정 상태 확인 (ACTIVE/DELETED)
    @Query(value =
            "SELECT COUNT(1) > 0 FROM TBL_USER u WHERE u.ID = :userId AND u.STATUS = 'ACTIVE'",
            nativeQuery = true)
    int existsActiveUserByUserId(@Param("userId") Long userId);

    // 게시물 ID, USER ID, 삭제 여부 확인 (Y/N)
    Optional<CommunityPost> findByPostIdAndUserIdAndIsDeleted(Long postId, Long userId, String isDeleted);

    // 게시물 ID, 삭제 여부 확인 (Y/N)
    Optional<CommunityPost> findByPostIdAndIsDeleted(Long postId, String isDeleted);

    // 게시물 조회수 1 증가 (DB 직접 업데이트)
    @Modifying
    @Query("UPDATE CommunityPost p SET p.viewCount = p.viewCount + 1 WHERE p.postId = :postId")
    void incrementViewCount(@Param("postId") Long postId);

    /**
     * 게시물 목록 조회
     */
    @Query(value =
            "SELECT p.POST_ID as postId, p.TITLE as title, u.NICKNAME as nickname, " +
                    "       p.VIEW_COUNT as viewCount, p.INSDATE as insDate, p.UPDDATE as updDate, a.FILE_URL as thumbnailUrl " +
                    "FROM TBL_COMMUNITY_POST p " +
                    "INNER JOIN TBL_USER u ON u.ID = p.USER_ID " +
                    "LEFT JOIN TBL_ATTACHMENT a ON a.REFERENCE_TYPE = '" + AttachmentReferenceType.COMMUNITY + "' AND a.REFERENCE_ID = p.POST_ID " +
                    "AND a.ATTACHMENT_ID = (SELECT MIN(a2.ATTACHMENT_ID) FROM TBL_ATTACHMENT a2 WHERE a2.REFERENCE_TYPE = '" + AttachmentReferenceType.COMMUNITY + "' AND a2.REFERENCE_ID = p.POST_ID) " +
                    "WHERE p.IS_DELETED = 'N' " +
                    "AND (:areaCd IS NULL OR p.AREACD = :areaCd) " +
                    "AND (:signguCds IS NULL OR p.SIGNGUCD IN (:signguCds)) " + // 괄호로 한 번 더 감싸서 확실하게 바인딩!
                    "ORDER BY p.POST_ID DESC " +
                    "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<CommunityPostListResponseDto> findPostListWithThumbnail(
            @Param("areaCd") String areaCd,
            @Param("signguCds") List<String> signguCds,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    long countByAreaCdAndSignguCdInAndIsDeleted(String areaCd, List<String> signguCds, String isDeleted);

    /**
     * 게시물 상세 조회
     */
    // 1. 게시물 상세 및 USER 닉네임 조인 조회
    @Query(value = """
        SELECT
            p.POST_ID as postId,
            p.USER_ID as userId,
            u.NICKNAME AS nickname,
            p.AREACD AS areaCd,
            p.SIGNGUCD AS signguCd,
            p.TITLE AS title,
            p.CONTENT AS content,
            p.VIEW_COUNT AS viewCount,
            p.INSDATE AS insDate,
            p.UPDDATE AS updDate
        FROM TBL_COMMUNITY_POST p
        LEFT JOIN TBL_USER u ON u.ID = p.USER_ID
        WHERE p.POST_ID = :postId AND p.IS_DELETED = 'N'    
        """, nativeQuery = true)
    Optional<Map<String, Object>> findPostDetailWithUser(@Param("postId") Long postId);

    // 2. 첨부파일 URL 목록 조회
    @Query("SELECT new com.amwohaji.backend.global.file.dto.AttachmentResponseDto(a.attachmentId, a.fileUrl) " +
            "FROM Attachment a " +
            "WHERE a.referenceType = :refType AND a.referenceId = :refId " +
            "ORDER BY a.attachmentId ASC")
    List<AttachmentResponseDto> findAttachmentsByReference(@Param("refType") String refType, @Param("refId") Long refId);
}
