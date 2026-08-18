package com.amwohaji.batch.job.AttractionInfoJob.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttractionInfoDto {
    private String attraction_id;               // 관광지 고유 ID
    private String baseYm;                      // 기준연월 (ex: 202605)
    private String title;                       // 관광지명 / 제목
    private String attraction_type_id;          // 관광타입 ID (12: 관광지, 14: 문화시설)

    // 위치 및 행정구역 정보 (X,Y 좌표 및 법정동 코드)
    private Double mapX;                        // X좌표 (경도)
    private Double mapY;                        // Y좌표 (위도)
    private String areaCd;                      // 지역코드 (ex: 11)
    private String areaNm;                      // 지역명 (ex: 서울특별시)
    private String signguCd;                    // 시군구 코드 (ex: 11110)
    private String signguNm;                    // 시군구명 (ex: 종로구)

    // 한국관광공사 표준 분류 체계
    private String category_l;                  // 분류체계 대분류
    private String category_m;                  // 분류체계 중분류
    private String category_s;                  // 분류체계 소분류

    // 주소 및 연락처
    private String addr1;                       // 주소
    private String addr2;                       // 상세주소
    private String zipcode;                     // 우편번호
    private String tel;                         // 전화번호

    // 이미지 자원 URL
    private String first_image;                 // 원본 대표이미지
    private String first_image2;                // 썸네일 대표이미지
}
