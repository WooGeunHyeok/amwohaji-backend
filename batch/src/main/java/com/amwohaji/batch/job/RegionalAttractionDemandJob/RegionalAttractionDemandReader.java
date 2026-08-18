package com.amwohaji.batch.job.RegionalAttractionDemandJob;

import com.amwohaji.batch.job.RegionalAttractionDemandJob.dto.RegionalAttractionDemandApiResponse;
import com.amwohaji.batch.job.RegionalAttractionDemandJob.dto.RegionalAttractionDemandDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

@Slf4j
@Component
public class RegionalAttractionDemandReader implements ItemReader<RegionalAttractionDemandDto> {

    // 서울특별시 지역코드 및 25개 시군구 코드
    private static final String AREA_CD = "11";
    private static final List<String> SEOUL_SIGNGU_CDS = List.of(
            "11110", "11140", "11170", "11200", "11215",
            "11230", "11260", "11290", "11305", "11320",
            "11350", "11380", "11410", "11440", "11470",
            "11500", "11530", "11545", "11560", "11590",
            "11620", "11650", "11680", "11710", "11740"
    );

    // 관광 서비스 지표 코드 1101 ~ 1112
    private static final List<String> ATTRACTION_SERVICE_CODES = List.of(
            "1101", "1102", "1103", "1104", "1105", "1106",
            "1107", "1108", "1109", "1110", "1111", "1112"
    );

    // 문화 자원 지표 코드 1201 ~ 1205
    private static final List<String> CULTURE_RESOURCE_CODES = List.of(
            "1201", "1202", "1203", "1204", "1205"
    );

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final int numOfRows = 100;
    private final String today;

    private final List<RegionalAttractionDemandDto> buffer = new ArrayList<>();
    private int signguIndex = 0;  // 현재 처리 중인 전국 타켓 인덱스
    private String currentTarget = "SERVICE";   // SERVICE -> CULTURE 순서로 진행
    private int codeIndex = 0;                  // 지표 코드 인덱스
    private int pageNo = 1;
    private boolean done = false;

    public RegionalAttractionDemandReader(
            WebClient webClient,
            ObjectMapper objectMapper,
            @Value("${openapi.api-key}") String apiKey) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.today = "202603";
    }

    @Override
    public RegionalAttractionDemandDto read() {
        if (buffer.isEmpty() && !done) {
            fetchNextPage();
        }
        if (buffer.isEmpty()) {
            return null;
        }
        return buffer.removeFirst();
    }

    private void fetchNextPage() {
        if (signguIndex >= SEOUL_SIGNGU_CDS.size()) {
            done = true;
            return;
        }

        String signguCd = SEOUL_SIGNGU_CDS.get(signguIndex);
        String requestUrl;

        if ("SERVICE".equals(currentTarget)) {
            String ixCd = ATTRACTION_SERVICE_CODES.get(codeIndex);
            requestUrl = String.format(
                    "https://apis.data.go.kr/B551011/AreaTarResDemService/areaTarSvcDemList" +
                            "?serviceKey=%s&pageNo=%d&numOfRows=%d&MobileOS=ETC&MobileApp=AppTest&_type=json&baseYm=%s&areaCd=%s&signguCd=%s&tarSvcDemIxCd=%s&_type=json",
                    apiKey, pageNo, numOfRows, today, AREA_CD, signguCd, ixCd);
        } else {
            String ixCd = CULTURE_RESOURCE_CODES.get(codeIndex);
            requestUrl = String.format(
                    "https://apis.data.go.kr/B551011/AreaTarResDemService/areaCulResDemList" +
                            "?serviceKey=%s&pageNo=%d&numOfRows=%d&MobileOS=ETC&MobileApp=AppTest&_type=json&baseYm=%s&areaCd=%s&signguCd=%s&culResDemIxCd=%s&_type=json",
                    apiKey, pageNo, numOfRows, today, AREA_CD, signguCd, ixCd);
        }


        log.info("지역별 관광 관광지 자원 수요 API 호출 - 시군구: {}, pageNo: {}", signguCd, pageNo);
        log.info("지역별 관광 관광지 자원 수요 API 호출 URL: {}", requestUrl);

        String rawBody;
        try {
            // 디도스 및 트래픽 유해 방지를 위해 호출 전 아주 미세한 쉬는시간 부여
            Thread.sleep(150);

            rawBody = webClient.get()
                    .uri(URI.create(requestUrl))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException(
                                            "API 클라이언트 오류 [" + clientResponse.statusCode() + "] - " + body)))
                    .onStatus(status -> status.is5xxServerError(), clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException(
                                            "API 서버 오류 [" + clientResponse.statusCode() + "] - " + body)))
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("API 호출 실패 - status: {}, body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new IllegalStateException("지역별 관광 관광지 자원 수요 API 호출 실패: " + e.getStatusCode(), e);
        } catch (IllegalStateException e) {
            log.error("API 호출 실패 - {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("API 호출 중 예상치 못한 오류 발생", e);
            throw new IllegalStateException("지역별 관광 관광지 자원 수요 API 호출 중 오류 발생", e);
        }

        if (rawBody == null || rawBody.isBlank()) {
            log.warn("API 응답 body가 비어있습니다. signguCd: {}, pageNo: {}", signguCd, pageNo);
            moveToNextSigngu();
            return;
        }

        if (!rawBody.trim().startsWith("{") && !rawBody.trim().startsWith("[")) {
            log.error("JSON이 아닌 응답 수신 - signguCd: {}, body: {}", signguCd, rawBody);
            throw new IllegalStateException("지역별 관광 관광지 자원 수요 API가 JSON이 아닌 응답을 반환했습니다: " + rawBody);
        }

        RegionalAttractionDemandApiResponse response;
        try {
            response = objectMapper.readValue(rawBody, RegionalAttractionDemandApiResponse.class);
        } catch (Exception e) {
            log.error("API 응답 파싱 실패 - body: {}", rawBody);
            throw new IllegalStateException("지역별 관광 관광지 자원 수요 API 응답 파싱 실패", e);
        }

        List<RegionalAttractionDemandApiResponse.Item> items = response.getItems();
        if (items.isEmpty()) {
            log.info("데이터 없음 - signguCd: {}, pageNo: {}", signguCd, pageNo);
            moveToNextSigngu();
            return;
        }

        int totalCount = response.getTotalCount();
        log.info("시군구: {}, 총 {}건 중 {}페이지 {}건 수신", signguCd, totalCount, pageNo, items.size());

        items.stream()
                .map(item -> {
                    RegionalAttractionDemandDto dto = new RegionalAttractionDemandDto();
                    dto.setBaseYm(item.getBaseYm());
                    dto.setAreaCd(item.getAreaCd());
                    dto.setAreaNm(item.getAreaNm());
                    dto.setSignguCd(item.getSignguCd());
                    dto.setSignguNm(item.getSignguNm());

                    if ("SERVICE".equals(currentTarget)) {
                        dto.setDemandDivCd("SERVICE");
                        dto.setTarSvcDemIxCd(item.getTarSvcDemIxCd());
                        dto.setTarSvcDemIxNm(item.getTarSvcDemIxNm());
                        dto.setTarSvcDemIxVal(item.getTarSvcDemIxVal());
                    } else {
                        dto.setDemandDivCd("CULTURE");
                        dto.setCulResDemIxCd(item.getCulResDemIxCd());
                        dto.setCulResDemIxNm(item.getCulResDemIxNm());
                        dto.setCulResDemIxVal(item.getCulResDemIxVal());
                    }
                    return dto;
                })
                .forEach(buffer::add);

        // 마지막 페이지면 다음 시군구로
        if (pageNo * numOfRows >= totalCount || items.size() < numOfRows) {
            moveToNextSigngu();
        } else {
            pageNo++;
        }

        return;
    }

    private void moveToNextSigngu() {
        signguIndex++;
        pageNo = 1;
        if (signguIndex >= SEOUL_SIGNGU_CDS.size()) {
            signguIndex = 0;
            codeIndex++;

            if ("SERVICE".equals(currentTarget) && codeIndex >= ATTRACTION_SERVICE_CODES.size()) {
                currentTarget = "CULTURE";      // 서비스 자원 -> 문화 자원 API 단계로 전환
                codeIndex =0;                   // 지표 코드 첫 번째 Index로 초기화 (1201 부터 시작)
            } else if ("CULTURE".equals(currentTarget) && codeIndex >= CULTURE_RESOURCE_CODES.size()) {
                done = true;                    // 문화 자원 API 호출 완료 후, 배치 종료
            }
        }
    }
}
