package com.amwohaji.batch.job.AttractionInfoJob;

import com.amwohaji.batch.entity.AttractionInfo;
import com.amwohaji.batch.job.AttractionInfoJob.dto.AttractionInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;
import java.util.Map;

@Slf4j
@Component
public class AttractionInfoProcessor implements ItemProcessor<AttractionInfoDto, AttractionInfo> {

    // 시군구명(areaNm) 매핑 테이블
    private static final Map<String, String> SIGNGU_MAP = Map.ofEntries(
            Map.entry("11110", "종로구"), Map.entry("11140", "중구"),
            Map.entry("11170", "용산구"), Map.entry("11200", "성동구"),
            Map.entry("11215", "광진구"), Map.entry("11230", "동대문구"),
            Map.entry("11260", "중랑구"), Map.entry("11290", "성북구"),
            Map.entry("11305", "강북구"), Map.entry("11320", "도봉구"),
            Map.entry("11350", "노원구"), Map.entry("11380", "은평구"),
            Map.entry("11410", "서대문구"), Map.entry("11440", "마포구"),
            Map.entry("11470", "양천구"), Map.entry("11500", "강서구"),
            Map.entry("11530", "구로구"), Map.entry("11545", "금천구"),
            Map.entry("11560", "영등포구"), Map.entry("11590", "동작구"),
            Map.entry("11620", "관악구"), Map.entry("11650", "서초구"),
            Map.entry("11680", "강남구"), Map.entry("11710", "송파구"),
            Map.entry("11740", "강동구")
    );
    @Override
    public AttractionInfo process(AttractionInfoDto dto) {
        if (dto.getAttraction_id() == null || dto.getAttraction_id().isBlank()) {
            return null;
        }

        // BaseYm (기준연월 가공) (ex:20250618095454 -> 202506)
        String formattedBaseYm = null;
        String rawModifiedTime = dto.getBaseYm();

        if (rawModifiedTime != null && rawModifiedTime.length() >= 6) {
            formattedBaseYm = rawModifiedTime.substring(0, 6);
        } else {
            return null;
        }

        // 시군구 코드 (3자치 (110) -> 5자리 (11110)) 및 시군구명 파싱
        String areaCd = "11";
        String areaNm = "서울특별시";
        String signguCd = null;
        String signguNm = "미분류";

        String rawSignguCd = dto.getSignguCd();
        if (rawSignguCd != null && !rawSignguCd.isBlank()) {
            String cleanSignguCd = rawSignguCd.trim();
            signguCd = areaCd + cleanSignguCd;

            signguNm = SIGNGU_MAP.getOrDefault(signguCd, "미분류");
        }

        return AttractionInfo.builder()
                .attraction_id(dto.getAttraction_id())
                .baseYm(formattedBaseYm)
                .title(dto.getTitle())
                .attraction_type_id(dto.getAttraction_type_id())
                .mapX(dto.getMapX())
                .mapY(dto.getMapY())
                .areaCd(dto.getAreaCd())
                .areaNm(areaNm)
                .signguCd(signguCd)
                .signguNm(signguNm)
                .category_l(dto.getCategory_l())
                .category_m(dto.getCategory_m())
                .category_s(dto.getCategory_s())
                .addr1(dto.getAddr1())
                .addr2(dto.getAddr2())
                .zipcode(dto.getZipcode())
                .tel(dto.getTel())
                .first_image(dto.getFirst_image())
                .first_image2(dto.getFirst_image2())
                .build();
    }
}
