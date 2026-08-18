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
public class CommunityPostUpdateRequestDto {
    /**
     * 게시물 수정 Request Dto
     */

    private String areaCd;
    private String signguCd;
    private String title;
    private String content;
    private List<Long> deleteAttachmentIds;
}
