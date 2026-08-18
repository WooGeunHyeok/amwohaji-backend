package com.amwohaji.batch.entity;

import com.amwohaji.batch.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RegionalAttractionDemand extends BaseEntity {

    // [공통 항목]
    private String baseYm;              // 기준연월
    private String areaCd;              // 지역코드
    private String areaNm;              // 지역명
    private String signguCd;            // 시군구 코드
    private String signguNm;            // 시군구 명
    private String demandDivCd;         // 수요 구분 코드 (SERVICE: 관광 서비스, CULTURE: 문화 자원)

    // [관광 서비스 수요 세부 지표 관련 항목]
    private String tarSvcDemIxCd;       // 관광 서비스 수요 지표 코드 (1101 ~ 1112)
    private String tarSvcDemIxNm;       // 관광 서비스 수요 세부 지표명
    private BigDecimal tarSvcDemIxVal;  // 관광 서비스 수요 세부 지표값

    // [문화 자원 수요 세부 지표 관련 항목]
    private String culResDemIxCd;       // 문화 자원 수요 지표 코드 (1201 ~ 1205)
    private String culResDemIxNm;       // 문화 자원 수요 세부 지표명
    private BigDecimal culResDemIxVal;  // 문화 자원 수요 세부 지표값
}
