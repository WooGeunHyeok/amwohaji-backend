package com.amwohaji.backend.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityPostListResponseDto {
    /**
     * 게시판 목록 조회 Response Dto
     */
    private Long postId;               // 게시물 고유 ID PK
    private String title;              // 게시물 제목
    private String nickname;           // 사용자 닉네임
    private int viewCount;             // 조회수
    private LocalDateTime insDate;   // 등록 일시 (시간 표출 용)
    private LocalDateTime updDate;   // 수정 일시 (시간 표출 용)
    private String fileUrl;            // 첨부파일 경로
}
