package com.amwohaji.batch.job.RegionalAttractionDemandJob;

import com.amwohaji.batch.entity.RegionalAttractionDemand;
import com.amwohaji.batch.job.RegionalAttractionDemandJob.dto.RegionalAttractionDemandDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RegionalAttractionDemandProcessor implements ItemProcessor<RegionalAttractionDemandDto, RegionalAttractionDemand> {
    private String currentTarget = "SERVICE";

    @Override
    public RegionalAttractionDemand process(RegionalAttractionDemandDto dto) {
        if (dto.getDemandDivCd().equals(currentTarget)) {
            if (dto.getTarSvcDemIxCd() == null || dto.getTarSvcDemIxCd().isBlank()) {
                return null;
            }
        } else if (dto.getCulResDemIxCd() == null || dto.getCulResDemIxCd().isBlank()) {
            return null;
        }

        // 아무런 가공이나 필터링 없이 그대로 빌드해서 넘김
        return RegionalAttractionDemand.builder()
                .baseYm(dto.getBaseYm())
                .areaCd(dto.getAreaCd())
                .areaNm(dto.getAreaNm())
                .signguCd(dto.getSignguCd())
                .signguNm(dto.getSignguNm())
                .demandDivCd(dto.getDemandDivCd())
                .tarSvcDemIxCd(dto.getTarSvcDemIxCd())
                .tarSvcDemIxNm(dto.getTarSvcDemIxNm())
                .tarSvcDemIxVal(dto.getTarSvcDemIxVal())
                .culResDemIxCd(dto.getCulResDemIxCd())
                .culResDemIxNm(dto.getCulResDemIxNm())
                .culResDemIxVal(dto.getCulResDemIxVal())
                .build();
    }
}
