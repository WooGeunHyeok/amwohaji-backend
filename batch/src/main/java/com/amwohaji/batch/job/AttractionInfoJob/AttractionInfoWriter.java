package com.amwohaji.batch.job.AttractionInfoJob;

import com.amwohaji.batch.entity.AttractionInfo;
import com.amwohaji.batch.mapper.AttractionInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttractionInfoWriter implements ItemWriter<AttractionInfo> {

    private final AttractionInfoMapper AttractionInfoMapper;


    @Override
    public void write(Chunk<? extends AttractionInfo> chunk) {
        // 1. Chunk로 넘어온 아이템 리스트 추출
        List<? extends AttractionInfo> items = chunk.getItems();

        int cnt = 0;

        for (AttractionInfo item : items) {
            cnt = AttractionInfoMapper.insertOrUpdateAttractionInfo(item);
        }

        if (cnt > 0) {
            log.info("작업 완료: {}건", cnt);
        }
    }
}
