package com.amwohaji.batch.job.AttractionResourceDemandJob;

import com.amwohaji.batch.entity.AttractionResourceDemand;
import com.amwohaji.batch.job.AttractionResourceDemandJob.dto.AttractionResourceDemandDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AttractionResourceDemandProcessor implements ItemProcessor<AttractionResourceDemandDto, AttractionResourceDemand> {

    @Override
    public AttractionResourceDemand process(AttractionResourceDemandDto dto) {
        if (dto.getHubTatsCd() == null || dto.getHubTatsCd().isBlank()) {
            return null;
        }

        return AttractionResourceDemand.builder()
                .hubTatsCd(dto.getHubTatsCd().trim())
                .baseYm(dto.getBaseYm().trim())
                .mapX(dto.getMapX())
                .mapY(dto.getMapY())
                .areaCd(dto.getAreaCd().trim())
                .areaNm(dto.getAreaNm().trim())
                .signguCd(dto.getSignguCd().trim())
                .signguNm(dto.getSignguNm().trim())
                .hubTatsNm(dto.getHubTatsNm().trim())
                .hubCtgryLclsNm(dto.getHubCtgryLclsNm().trim())
                .hubCtgryMclsNm(dto.getHubCtgryMclsNm().trim())
                .hubRank(dto.getHubRank())
                .build();
    }
}
