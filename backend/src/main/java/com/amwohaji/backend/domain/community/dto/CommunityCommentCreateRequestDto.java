package com.amwohaji.backend.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CommunityCommentCreateRequestDto {
    /**
     * 게시물 댓글 등록 Request Dto
     */
    private Long postId;                    // 게시물 ID
    private Long parentId;                  // 부모 댓글 ID
    private String content;                 // 내용
}
