package com.amwohaji.batch.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BatchTestController {

    private final JobLauncher jobLauncher;
    private final ApplicationContext applicationContext;

    @GetMapping("/batch/launch")
    public ResponseEntity<String> launchJob(@RequestParam("jobName") String jobName) {
        try {
            log.info(">>>> [Test] {} Job 실행 요청 접수", jobName);

            Job job = applicationContext.getBean(jobName, Job.class);
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .toJobParameters();

            // [핵심] Schedulers.boundedElastic() 스레드 풀에게 배치를 실행하라고 던집니다.
            // 이렇게 하면 reactor-http-nio 스레드는 블로킹되지 않고 즉시 통과합니다.
            Schedulers.boundedElastic().schedule(() -> {
                try {
                    log.info(">>>> [Test] 별도 스레드에서 배치 시작");
                    jobLauncher.run(job, jobParameters);
                    log.info(">>>> [Test] 별도 스레드에서 배치 종료");
                } catch (Exception e) {
                    log.error(">>>> [Test] 배치 실행 중 오류 발생: ", e);
                }
            });

            // Postman에는 즉시 응답이 가고, 배치는 백그라운드에서 안전하게 돕니다.
            return ResponseEntity.ok(jobName + " Job이 백그라운드 스레드에서 성공적으로 시작되었습니다. 콘솔 로그를 확인하세요.");

        } catch (Exception e) {
            log.error(">>>> [Test] Job 준비 중 에러 발생: ", e);
            return ResponseEntity.internalServerError().body("Job 실행 실패: " + e.getMessage());
        }
    }
}