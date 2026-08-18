package com.amwohaji.batch.job.AttractionInfoJob;


import com.amwohaji.batch.entity.AttractionInfo;
import com.amwohaji.batch.job.AttractionInfoJob.dto.AttractionInfoDto;
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
public class AttractionInfoJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final AttractionInfoReader reader;
    private final AttractionInfoWriter writer;
    private final AttractionInfoProcessor processor;

    @Bean
    public Job AttractionInfoJob() {
        return new JobBuilder("AttractionInfoJob", jobRepository)
                .start(saveAttractionInfoStep())
                .build();
    }

    @Bean
    public Step saveAttractionInfoStep() {
        return new StepBuilder("saveAttractionInfoStep", jobRepository)
                .<AttractionInfoDto, AttractionInfo>chunk(1000)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
