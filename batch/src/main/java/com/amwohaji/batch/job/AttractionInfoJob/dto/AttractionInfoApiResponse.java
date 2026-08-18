package com.amwohaji.batch.job.AttractionInfoJob.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttractionInfoApiResponse {
    private Response response;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        private Header header;
        private Body body;
    }

    // 성공/실패 정보를 담는 <header>
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    // 페이징 정보와 데이터 묶음을 담는 <body>
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        private Items items;
        private int numOfRows;
        private int pageNo;
        private int totalCount;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        private List<RawItem> item;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RawItem {
        private String contentid;          // 콘텐츠 ID
        private String title;              // 콘텐츠 제목
        private String contenttypeid;      // 관광타입 ID
        private String modifiedtime;       // 콘텐츠 수정일
        private Double mapx;               // X좌표
        private Double mapy;               // Y좌표
        private String lDongRegnCd;        // 지역코드
        private String lDongSignguCd;      // 시군구코드
        private String lclsSystm1;         // 분류체계 대분류
        private String lclsSystm2;         // 분류체계 중분류
        private String lclsSystm3;         // 분류체계 소분류
        private String addr1;              // 주소
        private String addr2;              // 상세주소
        private String zipcode;            // 우편번호
        private String tel;                // 전화번호
        private String firstimage;         // 원본 대표이미지
        private String firstimage2;        // 썸네일 대표이미지
    }

    public String getResultCode() {
        if (response == null || response.getHeader() == null) return null;
        return response.getHeader().getResultCode();
    }

    public int getTotalCount() {
        if (response == null || response.getBody() == null) return 0;
        return response.getBody().getTotalCount();
    }

    public List<RawItem> getItems() {
        if (response == null || response.getBody() == null
                || response.getBody().getItems() == null
                || response.getBody().getItems().getItem() == null) {
            return List.of();
        }
        return response.getBody().getItems().getItem();
    }
}
