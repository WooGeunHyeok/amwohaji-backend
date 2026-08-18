package com.amwohaji.batch.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    COMMON_ERROR("C001", "서버 오류"),
    API_ERROR("A001", "외부 API 호출 오류"),
    DB_ERROR("D001", "DB 처리 오류"),
    BATCH_ERROR("B001", "배치 처리 오류");

    private final String code;
    private final String message;
}
