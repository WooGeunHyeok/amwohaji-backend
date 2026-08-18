package com.amwohaji.batch.job.AttractionInfoJob;

import com.amwohaji.batch.job.AttractionInfoJob.dto.AttractionInfoApiResponse;
import com.amwohaji.batch.job.AttractionInfoJob.dto.AttractionInfoDto;
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

@Slf4j
@Component
public class AttractionInfoReader implements ItemReader<AttractionInfoDto> {

    // 서울특별시 지역코드 및 25개 시군구 코드
    private static final String AREA_CD = "11";
    private static final List<String> SEOUL_SIGNGU_CDS = List.of(
            "110", "140", "170", "200", "215",
            "230", "260", "290", "305", "320",
            "350", "380", "410", "440", "470",
            "500", "530", "545", "560", "590",
            "620", "650", "680", "710", "740"
    );

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final int numOfRows = 100;

    private final List<AttractionInfoDto> buffer = new ArrayList<>();
    private int signguIndex = 0;  // 현재 처리 중인 전국 타켓 인덱스
    private int pageNo = 1;
    private boolean done = false;

    public AttractionInfoReader(
            WebClient webClient,
            ObjectMapper objectMapper,
            @Value("${openapi.api-key}") String apiKey) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
//        this.today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
    }

    @Override
    public AttractionInfoDto read() {
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
        String requestUrl = String.format(
                "https://apis.data.go.kr/B551011/KorService2/areaBasedList2" +
                        "?serviceKey=%s&pageNo=%d&numOfRows=%d&MobileOS=ETC&MobileApp=AppTest&lDongRegnCd=%s&lDongSignguCd=%s&_type=json",
                apiKey, pageNo, numOfRows, AREA_CD, signguCd);

        log.info("관광지 정보 API 호출 - 시군구: {}, pageNo: {}", signguCd, pageNo);
        log.info("관광지 정보 API 호출 URL: {}", requestUrl);

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
            throw new IllegalStateException("관광지 정보 API 호출 실패: " + e.getStatusCode(), e);
        } catch (IllegalStateException e) {
            log.error("API 호출 실패 - {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("API 호출 중 예상치 못한 오류 발생", e);
            throw new IllegalStateException("관광지 정보 API 호출 중 오류 발생", e);
        }

        if (rawBody == null || rawBody.isBlank()) {
            log.warn("API 응답 body가 비어있습니다. signguCd: {}, pageNo: {}", signguCd, pageNo);
            moveToNextSigngu();
            return;
        }

        if (!rawBody.trim().startsWith("{") && !rawBody.trim().startsWith("[")) {
            log.error("JSON이 아닌 응답 수신 - signguCd: {}, body: {}", signguCd, rawBody);
            throw new IllegalStateException("관광지 정보 API가 JSON이 아닌 응답을 반환했습니다: " + rawBody);
        }

        AttractionInfoApiResponse response;
        try {
            response = objectMapper.readValue(rawBody, AttractionInfoApiResponse.class);
        } catch (Exception e) {
            log.error("API 응답 파싱 실패 - body: {}", rawBody);
            throw new IllegalStateException("관광지 정보 API 응답 파싱 실패", e);
        }

        List<AttractionInfoApiResponse.RawItem> items = response.getItems();
        if (items.isEmpty()) {
            log.info("데이터 없음 - signguCd: {}, pageNo: {}", signguCd, pageNo);
            moveToNextSigngu();
            return;
        }

        int totalCount = response.getTotalCount();
        log.info("시군구: {}, 총 {}건 중 {}페이지 {}건 수신", signguCd, totalCount, pageNo, items.size());

        items.stream()
                .map(item -> {
                    AttractionInfoDto dto = new AttractionInfoDto();
                    dto.setAttraction_id(item.getContentid());
                    dto.setBaseYm(item.getModifiedtime());
                    dto.setTitle(item.getTitle());
                    dto.setAttraction_type_id(item.getContenttypeid());
                    dto.setMapX(item.getMapx());
                    dto.setMapY(item.getMapy());
                    dto.setAreaCd(item.getLDongRegnCd());
                    dto.setSignguCd(item.getLDongSignguCd());
                    dto.setCategory_l(item.getLclsSystm1());
                    dto.setCategory_m(item.getLclsSystm2());
                    dto.setCategory_s(item.getLclsSystm3());
                    dto.setAddr1(item.getAddr1());
                    dto.setAddr2(item.getAddr2());
                    dto.setZipcode(item.getZipcode());
                    dto.setTel(item.getTel());
                    dto.setFirst_image(item.getFirstimage());
                    dto.setFirst_image2(item.getFirstimage2());
                    return dto;
                })
                .forEach(buffer::add);

        if (pageNo * numOfRows >= totalCount || items.size() < numOfRows) {
            moveToNextSigngu();
        } else {
            pageNo++;
        }
    }

    private void moveToNextSigngu() {
        signguIndex++;
        pageNo = 1;
        if (signguIndex >= SEOUL_SIGNGU_CDS.size()) {
            done = true;
        }
    }
}
