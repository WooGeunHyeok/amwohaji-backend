package com.amwohaji.batch.job.AttractionResourceDemandJob;

import com.amwohaji.batch.entity.AttractionResourceDemand;
import com.amwohaji.batch.job.AttractionResourceDemandJob.dto.AttractionResourceDemandDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AttractionResourceDemandJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final AttractionResourceDemandReader reader;
    private final AttractionResourceDemandProcessor processor;
    private final AttractionResourceDemandWriter writer;

    @Bean
    public Job attractionResourceDemandJob() {
        return new JobBuilder("attractionResourceDemandJob", jobRepository)
                .start(saveAttractionResourceDemandStep())
                .build();
    }

    @Bean
    public Step saveAttractionResourceDemandStep() {
        return new StepBuilder("saveAttractionResourceDemandStep", jobRepository)
                .<AttractionResourceDemandDto, AttractionResourceDemand>chunk(1000)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
