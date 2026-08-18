package com.amwohaji.backend.domain.community.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommunityPostCreateRequestDto {
    /**
     *  등록 Request Dto
     */
    private String areaCd;
    private String signguCd;
    private String title;
    private String content;
}
