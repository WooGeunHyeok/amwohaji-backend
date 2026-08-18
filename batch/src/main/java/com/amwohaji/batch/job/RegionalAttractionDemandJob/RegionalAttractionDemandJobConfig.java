package com.amwohaji.batch.job.RegionalAttractionDemandJob;

import com.amwohaji.batch.entity.RegionalAttractionDemand;
import com.amwohaji.batch.job.RegionalAttractionDemandJob.dto.RegionalAttractionDemandDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RegionalAttractionDemandJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // 우리가 만든 3대 컴포넌트를 주입받습니다.
    private final RegionalAttractionDemandReader reader;
    private final RegionalAttractionDemandProcessor processor;
    private final RegionalAttractionDemandWriter writer;

    @Bean
    public Job regionalAttractionDemandJob() {
        return new JobBuilder("regionalAttractionDemandJob", jobRepository)
                .start(saveRegionalAttractionDemandStep())
                .build();
    }

    @Bean
    public Step saveRegionalAttractionDemandStep() {
        return new StepBuilder("saveRegionalAttractionDemandStep", jobRepository)
                .<RegionalAttractionDemandDto, RegionalAttractionDemand>chunk(100)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}