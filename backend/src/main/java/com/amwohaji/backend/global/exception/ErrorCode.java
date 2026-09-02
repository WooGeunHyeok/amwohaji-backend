package com.amwohaji.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "입력값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "허용되지 않은 HTTP 메서드입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "존재하지 않는 리소스입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "서버 오류가 발생했습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C005", "입력 타입이 올바르지 않습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "접근 권한이 없습니다."),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "만료된 토큰입니다."),

    // Bookmark
    DUPLICATE_BOOKMARK(HttpStatus.CONFLICT, "B001", "이미 북마크한 관광지입니다."),
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "B002", "존재하지 않는 북마크입니다."),

    // File
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "F001", "파일이 비어 있습니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "F002", "파일 용량이 허용 범위를 초과했습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "F003", "파일 업로드에 실패했습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "F004", "파일 삭제에 실패했습니다."),
    ATTACHMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "F005", "존재하지 않는 첨부파일입니다."),
    INVALID_ATTACHMENT_DELETE_REQUEST(HttpStatus.BAD_REQUEST, "F006", "삭제 권한이 없는 첨부파일이 포함되어 있습니다."),

    // Community Post
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "CP001", "존재하지 않거나 탈퇴한 회원입니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "CP002", "존재하지 않는 게시물입니다."),

    // Community Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CC001", "존재하지 않거나 회원 정보가 불일치 합니다."),

    // Like
    DUPLICATE_LIKE(HttpStatus.CONFLICT, "L001", "이미 좋아요를 누른 항목입니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "L002", "좋아요를 누르지 않은 항목입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
