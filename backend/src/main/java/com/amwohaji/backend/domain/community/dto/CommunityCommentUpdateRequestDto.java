package com.amwohaji.backend.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityCommentUpdateRequestDto {
    /**
     * 게시물 댓글 수정 Request Dto
     */

    private String content;
    private List<Long> deleteAttachmentIds;
}
