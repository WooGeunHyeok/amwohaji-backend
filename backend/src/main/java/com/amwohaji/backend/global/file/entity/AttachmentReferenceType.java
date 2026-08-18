package com.amwohaji.backend.global.file.entity;

/**
 * TBL_ATTACHMENT.REFERENCE_TYPE에 넣을 값들.
 * 이넘이 아닌 문자열 상수로 관리 — REFERENCE_TYPE 값 자체를 연관 테이블명(TBL_COMMUNITY 등)으로 사용한다.
 * 새로운 도메인에서 첨부파일을 쓰게 되면 여기에 상수를 추가한다.
 */
public final class AttachmentReferenceType {

    public static final String COMMUNITY = "TBL_COMMUNITY";
    public static final String COMMENT = "TBL_COMMENT";
    public static final String PROFILE = "TBL_PROFILE";
    public static final String NOTICE = "TBL_NOTICE";

    private AttachmentReferenceType() {
    }
}
