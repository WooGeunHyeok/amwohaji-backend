package com.amwohaji.backend.domain.mypage.repository;

import com.amwohaji.backend.domain.community.dto.CommunityPostListResponseDto;
import com.amwohaji.backend.global.file.entity.AttachmentReferenceType;
import com.amwohaji.backend.global.like.entity.Like;
import com.amwohaji.backend.global.like.entity.LikeReferenceType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyPageRepository extends Repository<Like, Long> {

    /**
     * 마이페이지 - 내가 쓴 글 목록 조회
     */
    @Query(value =
            "SELECT p.POST_ID as postId, p.TITLE as title, u.NICKNAME as nickname, " +
                    "       p.VIEW_COUNT as viewCount, p.INSDATE as insDate, p.UPDDATE as updDate, a.FILE_URL as thumbnailUrl " +
                    "FROM TBL_COMMUNITY_POST p " +
                    "INNER JOIN TBL_USER u ON u.ID = p.USER_ID " +
                    "LEFT JOIN TBL_ATTACHMENT a ON a.REFERENCE_TYPE = '" + AttachmentReferenceType.COMMUNITY + "' AND a.REFERENCE_ID = p.POST_ID " +
                    "AND a.ATTACHMENT_ID = (SELECT MIN(a2.ATTACHMENT_ID) FROM TBL_ATTACHMENT a2 WHERE a2.REFERENCE_TYPE = '" + AttachmentReferenceType.COMMUNITY + "' AND a2.REFERENCE_ID = p.POST_ID) " +
                    "WHERE p.USER_ID = :userId AND p.IS_DELETED = 'N' " +
                    "ORDER BY p.POST_ID DESC " +
                    "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<CommunityPostListResponseDto> findMyPostListWithThumbnail(
            @Param("userId") Long userId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value =
            "SELECT COUNT(1) FROM TBL_COMMUNITY_POST p " +
                    "WHERE p.USER_ID = :userId AND p.IS_DELETED = 'N'",
            nativeQuery = true)
    long countMyPosts(@Param("userId") Long userId);

    /**
     * 마이페이지 - 좋아요 누른 게시물 목록 조회 (최근 좋아요 순)
     */
    @Query(value =
            "SELECT p.POST_ID as postId, p.TITLE as title, u.NICKNAME as nickname, " +
                    "       p.VIEW_COUNT as viewCount, p.INSDATE as insDate, p.UPDDATE as updDate, a.FILE_URL as thumbnailUrl " +
                    "FROM TBL_LIKE l " +
                    "INNER JOIN TBL_COMMUNITY_POST p ON p.POST_ID = l.REFERENCE_ID AND l.REFERENCE_TYPE = '" + LikeReferenceType.COMMUNITY_POST + "' " +
                    "INNER JOIN TBL_USER u ON u.ID = p.USER_ID " +
                    "LEFT JOIN TBL_ATTACHMENT a ON a.REFERENCE_TYPE = '" + AttachmentReferenceType.COMMUNITY + "' AND a.REFERENCE_ID = p.POST_ID " +
                    "AND a.ATTACHMENT_ID = (SELECT MIN(a2.ATTACHMENT_ID) FROM TBL_ATTACHMENT a2 WHERE a2.REFERENCE_TYPE = '" + AttachmentReferenceType.COMMUNITY + "' AND a2.REFERENCE_ID = p.POST_ID) " +
                    "WHERE l.USER_ID = :userId AND p.IS_DELETED = 'N' " +
                    "ORDER BY l.LIKE_ID DESC " +
                    "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<CommunityPostListResponseDto> findLikedPostListWithThumbnail(
            @Param("userId") Long userId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value =
            "SELECT COUNT(1) FROM TBL_LIKE l " +
                    "INNER JOIN TBL_COMMUNITY_POST p ON p.POST_ID = l.REFERENCE_ID " +
                    "WHERE l.USER_ID = :userId AND l.REFERENCE_TYPE = '" + LikeReferenceType.COMMUNITY_POST + "' AND p.IS_DELETED = 'N'",
            nativeQuery = true)
    long countLikedPostsByUserId(@Param("userId") Long userId);
}
