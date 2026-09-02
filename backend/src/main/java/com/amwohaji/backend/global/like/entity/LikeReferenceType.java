package com.amwohaji.backend.global.like.entity;

/**
 * TBL_LIKE.REFERENCE_TYPE에 넣을 값들.
 * 이넘이 아닌 문자열 상수로 관리 — REFERENCE_TYPE 값 자체를 연관 테이블명(TBL_COMMUNITY_POST 등)으로 사용한다.
 * 새로운 도메인(추천 코스 등)에서 좋아요를 쓰게 되면 여기에 상수를 추가한다.
 */
public final class LikeReferenceType {

    public static final String COMMUNITY_POST = "TBL_COMMUNITY_POST";
    public static final String COMMUNITY_COMMENT = "TBL_COMMUNITY_COMMENT";
}
