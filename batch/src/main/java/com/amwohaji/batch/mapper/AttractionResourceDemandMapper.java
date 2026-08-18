package com.amwohaji.batch.mapper;

import com.amwohaji.batch.entity.AttractionResourceDemand;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AttractionResourceDemandMapper {

    AttractionResourceDemand findByCode(@Param("hubTatsCd") String code);

    List<AttractionResourceDemand> findAllByCodes(@Param("hubTatsCds") List<String> codes);

    void insert(AttractionResourceDemand AttractionResourceDemand);

    void update(AttractionResourceDemand AttractionResourceDemand);
}
