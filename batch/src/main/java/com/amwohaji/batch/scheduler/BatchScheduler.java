package com.amwohaji.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobOperator jobOperator;
    private final Job legalDongCodeJob;
    private final Job tatsCnctrRateJob;
    private final Job regnVisitrDDJob;

    // 매주 월요일 새벽 2시 실행
    @Scheduled(cron = "0 0 2 * * MON")
    public void runLegalDongCodeJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLocalDateTime("runAt", LocalDateTime.now())
                    .toJobParameters();

            log.info("법정동 코드 배치 시작: {}", LocalDateTime.now());
            jobOperator.start(legalDongCodeJob, params);
        } catch (Exception e) {
            log.error("법정동 코드 배치 실패", e);
        }
    }

    // 매일 새벽 3시 실행
    @Scheduled(cron = "0 0 3 * * *")
    public void runTatsCnctrRateJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLocalDateTime("runAt", LocalDateTime.now())
                    .toJobParameters();

            log.info("관광지 예측 정보 배치 시작: {}", LocalDateTime.now());
            jobOperator.start(tatsCnctrRateJob, params);
        } catch (Exception e) {
            log.error("관광지 예측 정보 배치 실패", e);
        }
    }

    // 매일 새벽 4시 실행
    @Scheduled(cron = "0 0 4 * * *")
    public void runRegnVisitrDDJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLocalDateTime("runAt", LocalDateTime.now())
                    .toJobParameters();

            log.info("지자체 지역방문자수 정보 배치 시작: {}", LocalDateTime.now());
            jobOperator.start(regnVisitrDDJob, params);
        } catch (Exception e) {
            log.error("지자체 지역방문자수 정보 배치 실패", e);
        }
    }
}
