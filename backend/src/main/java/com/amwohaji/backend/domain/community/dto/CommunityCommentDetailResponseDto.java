package com.amwohaji.backend.domain.community.dto;

import com.amwohaji.backend.global.file.dto.AttachmentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityCommentDetailResponseDto {
    /**
     * 게시물 상세 조회
     */
    private Long commentId;                             // 댓글 고유 ID (PK)
    private String nickname;                            // 사용자 닉네임
    private Long parentId;                              // 부모 댓글 ID (대댓글용 PID)
    private String content;                             // 댓글 내용
    private LocalDateTime insDate;                      // 등록 일시
    private LocalDateTime updDate;                      // 수정 일시
    private List<AttachmentResponseDto> attachments;    // 첨부파일 목록 (attachmentId, fileUrl)
    private boolean isOwner;                            // 본인 작성 여부

    // 대댓글(자식 댓글) 리스트를 저장할 필드
    @Builder.Default
    private List<CommunityCommentDetailResponseDto> children = new ArrayList<>();
}
