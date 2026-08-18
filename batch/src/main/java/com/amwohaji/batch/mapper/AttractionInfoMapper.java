package com.amwohaji.batch.mapper;

import com.amwohaji.batch.entity.AttractionInfo;

public interface AttractionInfoMapper {
    /**
     * 관광지 정보 대량 Insert 처리
     */
    int insertOrUpdateAttractionInfo(AttractionInfo AttractionInfo);
}
