package com.amwohaji.batch.job.AttractionResourceDemandJob;

import com.amwohaji.batch.entity.AttractionResourceDemand;
import com.amwohaji.batch.mapper.AttractionResourceDemandMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttractionResourceDemandWriter implements ItemWriter<AttractionResourceDemand> {

    private final AttractionResourceDemandMapper AttractionResourceDemandMapper;

    @Override
    public void write(Chunk<? extends AttractionResourceDemand> chunk) {
        List<? extends AttractionResourceDemand> items = chunk.getItems();

        // API에서 불러온 중심지관광지코드 (hudTatsCd) 목록 추출
        List<String> codes = items.stream()
                .map(AttractionResourceDemand::getHubTatsCd)
                .toList();

        // 기존 데이터 한 번에 조회 (Maira DB)
        Map<String, AttractionResourceDemand> existingMap = AttractionResourceDemandMapper.findAllByCodes(codes)
                .stream()
                .collect(Collectors.toMap(
                        AttractionResourceDemand::getHubTatsCd, e -> e));

        int insertCount = 0;
        int updateCount = 0;

        for (AttractionResourceDemand item : items) {
            AttractionResourceDemand existing = existingMap.get(item.getHubTatsCd());

            if (existing == null) {
                // 신규 insert
                AttractionResourceDemand toInsert = AttractionResourceDemand.builder()
                        .hubTatsCd(item.getHubTatsCd())
                        .baseYm(item.getBaseYm())
                        .mapX(item.getMapX())
                        .mapY(item.getMapY())
                        .areaCd(item.getAreaCd())
                        .areaNm(item.getAreaNm())
                        .signguCd(item.getSignguCd())
                        .signguNm(item.getSignguNm())
                        .hubTatsNm(item.getHubTatsNm())
                        .hubCtgryLclsNm(item.getHubCtgryLclsNm())
                        .hubCtgryMclsNm(item.getHubCtgryMclsNm())
                        .hubRank(item.getHubRank())

                        // BaseEntity 공통 칼럼
                        .insdate(LocalDateTime.now())
                        .insid("BATCH")
                        .upddate(LocalDateTime.now())
                        .updid("BATCH")
                        .build();
                AttractionResourceDemandMapper.insert(toInsert);
                insertCount++;

            } else if (isChanged(existing, item)) {
                // 변경된 경우만 update
                AttractionResourceDemand toUpdate = AttractionResourceDemand.builder()
                        .hubTatsCd(item.getHubTatsCd())
                        .baseYm(item.getBaseYm())
                        .mapX(item.getMapX())
                        .mapY(item.getMapY())
                        .areaCd(item.getAreaCd())
                        .areaNm(item.getAreaNm())
                        .signguCd(item.getSignguCd())
                        .signguNm(item.getSignguNm())
                        .hubTatsNm(item.getHubTatsNm())
                        .hubCtgryLclsNm(item.getHubCtgryLclsNm())
                        .hubCtgryMclsNm(item.getHubCtgryMclsNm())
                        .hubRank(item.getHubRank())

                        .upddate(LocalDateTime.now())
                        .updid("BATCH")
                        .build();
                AttractionResourceDemandMapper.update(toUpdate);
                updateCount++;
            }
        }

        if (insertCount > 0 || updateCount > 0) {
            log.info("Insert: {}건, Update: {}건", insertCount, updateCount);
        }
    }

    // 12 항복 중 변경된 값 확인 함수
    private boolean isChanged(AttractionResourceDemand existing, AttractionResourceDemand item) {
        return !existing.getBaseYm().equals(item.getBaseYm())
                || !existing.getMapX().equals(item.getMapX())
                || !existing.getMapY().equals(item.getMapY())
                || !existing.getAreaCd().equals(item.getAreaCd())
                || !existing.getAreaNm().equals(item.getAreaNm())
                || !existing.getSignguCd().equals(item.getSignguCd())
                || !existing.getSignguNm().equals(item.getSignguNm())
                || !existing.getHubTatsNm().equals(item.getHubTatsNm())
                || !existing.getHubCtgryLclsNm().equals(item.getHubCtgryLclsNm())
                || !existing.getHubCtgryMclsNm().equals(item.getHubCtgryMclsNm())
                || existing.getHubRank() != item.getHubRank();
    }
}
