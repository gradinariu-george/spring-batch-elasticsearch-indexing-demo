package com.example.springbatchdemo.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Log4j2
public class JobService {
    private final JobLauncher jobLauncher;

    private final Job dataPopulationJob;

    private final Job indexingElasticJob;


    @Async
    public void populateDatabaseJobTrigger() {
        try {
            log.info("Data population job has been triggered.");

            JobParameters jobParameters = new JobParametersBuilder().addLong("startAt", System.currentTimeMillis()).toJobParameters();
            jobLauncher.run(dataPopulationJob, jobParameters);
            log.info("Data population job has ended triggered.");

        } catch (Exception e) {
            log.error("Failed to trigger data population job.", e);
        }
    }


    @Async
    public void indexDomainEntitiesToElasticsearch() {
        try {
            log.info("Import indexing job has been triggered.");
            JobParameters jobParameters = new JobParametersBuilder().addLong("startAt", System.currentTimeMillis()).toJobParameters();
            jobLauncher.run(indexingElasticJob, jobParameters);
            log.info("Import indexing job has ended triggered.");
        } catch (Exception e) {
            log.error("Failed to trigger indexing job.", e);
        }
    }
}
