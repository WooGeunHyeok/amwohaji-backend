package com.amwohaji.batch.job.RegionalAttractionDemandJob.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegionalAttractionDemandApiResponse {
    // 큰 껍데기 <response>
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

    // 여러 개의 <item> 리스트를 품는 <items>
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        private List<Item> item; // 여러 개가 연속으로 들어오면 이 리스트에 차곡차곡 쌓입니다.
    }

    // [핵심 변경] 질문자님의 중심관광지 정보 12개 알맹이 필드 선언 <item>
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        // [공통 항목]
        private String baseYm;               // 기준연월
        private String areaCd;               // 지역코드
        private String areaNm;               // 지역명
        private String signguCd;             // 시군구 코드
        private String signguNm;             // 시군구 명
        private String demandDivCd;          // 수요 구분 코드 (SERVICE: 관광 서비스, CULTURE: 문화 자원)

        // [관광 서비스 수요 세부 지표 관련 항목]
        private String tarSvcDemIxCd;        // 관광 서비스 수요 지표 코드 (1101 ~ 1112)
        private String tarSvcDemIxNm;        // 관광 서비스 수요 세부 지표명
        private BigDecimal tarSvcDemIxVal;   // 관광 서비스 수요 세부 지표값

        // [문화 자원 수요 세부 지표 관련 항목]
        private String culResDemIxCd;        // 문화 자원 수요 지표 코드 (1201 ~ 1205)
        private String culResDemIxNm;        // 문화 자원 수요 세부 지표명
        private BigDecimal  culResDemIxVal;  // 문화 자원 수요 세부 지표값
    }

    // --- 편리하게 알맹이만 쏙 빼 쓰기 위한 편의 메서드 3종 ---

    public String getResultCode() {
        if (response == null || response.getHeader() == null) return null;
        return response.getHeader().getResultCode();
    }

    public int getTotalCount() {
        if (response == null || response.getBody() == null) return 0;
        return response.getBody().getTotalCount();
    }

    public List<Item> getItems() {
        if (response == null || response.getBody() == null
                || response.getBody().getItems() == null
                || response.getBody().getItems().getItem() == null) {
            return List.of();
        }
        return response.getBody().getItems().getItem();
    }
}
