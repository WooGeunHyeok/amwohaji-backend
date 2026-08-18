package com.amwohaji.batch.job.AttractionResourceDemandJob.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttractionResourceDemandDto {
    private String hubTatsCd;           // 중심지관광지코드 (메인 고유키)
    private String baseYm;              // 기준연월
    private Double mapX;                // X좌표 값
    private Double mapY;                // Y좌표 값
    private String areaCd;              // 도시코드
    private String areaNm;              // 도시명
    private String signguCd;            // 지역코드
    private String signguNm;            // 지역명
    private String hubTatsNm;           // 중심지관광지명 (현대백화점)
    private String hubCtgryLclsNm;      // 중심지카테고리대분류명 (관광지)
    private String hubCtgryMclsNm;      // 중심지카테고리중분류명 (쇼핑)
    private int hubRank;                // 중심지 순위
}
