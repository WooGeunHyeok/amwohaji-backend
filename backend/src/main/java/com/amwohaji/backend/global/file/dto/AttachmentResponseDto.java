package com.amwohaji.backend.global.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AttachmentResponseDto {

    /**
     * 첨부파일 Response Dto
     */

    private Long attachmentId;
    private String fileUrl;
}
