package com.amwohaji.backend.domain.community.dto;

import com.amwohaji.backend.global.file.dto.AttachmentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CommunityPostDetailResponseDto {
    /**
     * 게시판 상세 조회 Response Dto
     */
    private Long postId;                                // 게시물 ID (PK)
    private String nickname;                            // 사용자 닉네임 (TBL_USER 조인)
    private String areaCd;                              // 지역 코드
    private String signguCd;                            // 시군구 코드
    private String title;                               // 게시물 제목
    private String content;                             // 게시물 본문 내용
    private Long viewCount;                             // 게시물 조회수
    private LocalDateTime insDate;                      // 작성 일시
    private LocalDateTime updDate;                      // 수정 일시
    private List<AttachmentResponseDto> attachments;    // 첨부파일 목록 (attachmentId, fileUrl)
    private boolean isOwner;                            // 본인 작성 여부 플래그
}
