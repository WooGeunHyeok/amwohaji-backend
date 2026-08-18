package com.amwohaji.backend.domain.community.repository;

import com.amwohaji.backend.domain.community.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    // USER ID 본인 확인, 삭제 여부 (Y/N) 확인
    Optional<CommunityComment> findByCommentIdAndUserIdAndIsDeleted(Long commentId, Long userId, String isDeleted);

    // 댓글 존재 및 삭제 여부 확인
    Optional<CommunityComment> findByCommentIdAndIsDeleted(Long commentId, String isDeleted);

    /**
     * 게시물 댓글/대댓글 삭제 (본인 댓글만 삭제 가능)
     */
    @Modifying
    @Query(value =
            "UPDATE TBL_COMMUNITY_COMMENT " +
            "SET IS_DELETED = 'Y', UPDDATE = CURRENT_TIMESTAMP " +
            "WHERE COMMENT_ID = :commentId ",
            nativeQuery = true)
    void deleteComment(@Param("commentId") Long commentId);

    /**
     * 게시물 댓글 조회
     */
    @Query(value = """
        SELECT
                c.COMMENT_ID AS commentId,
                c.POST_ID AS postId,
                c.USER_ID AS userId,     
                c.PARENT_ID AS parentId,
                u.NICKNAME AS nickname,
                c.CONTENT AS content,
                c.IS_DELETED AS isDeleted,
                c.INSDATE AS insDate
        FROM TBL_COMMUNITY_COMMENT c
        LEFT JOIN TBL_USER u ON u.ID = c.USER_ID
        WHERE c.POST_ID = :postId AND c.IS_DELETED = 'N'
        ORDER BY c.INSDATE ASC, c.COMMENT_ID ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findCommentByPostId(@Param("postId") Long postId);
}
