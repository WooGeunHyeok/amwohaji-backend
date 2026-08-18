package com.amwohaji.batch.job.RegionalAttractionDemandJob;

import com.amwohaji.batch.entity.RegionalAttractionDemand;
import com.amwohaji.batch.mapper.RegionalAttractionDemandMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegionalAttractionDemandWriter implements ItemWriter<RegionalAttractionDemand> {
    private final RegionalAttractionDemandMapper regionalAttractionDemandMapper;

    @Override
    public void write(Chunk<? extends RegionalAttractionDemand> chunk) {
        // 1. Chunk 바구니에서 가공이 끝난 엔티티 리스트를 꺼냅니다.
        List<? extends RegionalAttractionDemand> items = chunk.getItems();

        int totalSavedCount = 0;

        // 2. 루프를 돌며 제트슨 나노 DB에 Upsert(INSERT ON DUPLICATE KEY UPDATE)를 실행합니다.
        for (RegionalAttractionDemand item : items) {
            // 주입 및 수정 성공 시 1 또는 2를 반환하므로 성공 시 카운트를 올립니다.
            int result = regionalAttractionDemandMapper.insertOrUpdateRegionalAttractionDemand(item);
            if (result > 0) {
                totalSavedCount++;
            }
        }

        // 3. 이번 청크(Chunk) 단위 작업 결과를 로그로 예쁘게 남깁니다.
        if (totalSavedCount > 0) {
            log.info("[RegionalAttractionDemandWriter] 이번 청크 적재 완료: 총 {}건 (성공)", totalSavedCount);
        }
    }
}
