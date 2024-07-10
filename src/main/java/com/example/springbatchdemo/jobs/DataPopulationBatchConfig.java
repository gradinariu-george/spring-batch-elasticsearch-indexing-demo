package com.example.springbatchdemo.jobs;


import com.example.springbatchdemo.config.JobLoggerListener;
import com.example.springbatchdemo.domain.DomainEntity;
import com.example.springbatchdemo.domain.DomainEntityRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Log4j2
public class DataPopulationBatchConfig {

    private final DomainEntityRepository postgresRepository;

    public DataPopulationBatchConfig(DomainEntityRepository postgresRepository) {
        this.postgresRepository = postgresRepository;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }


    @Bean
    public TaskExecutor taskExecutorAsyncDomain() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(10); // Number of threads
        return taskExecutor;
    }

    @Bean
    public Job dataPopulationJob(JobRepository jobRepository, Step populateDatabase) {
        return new JobBuilder("dataPopulationJob", jobRepository)
                .listener(new JobLoggerListener())
                .start(populateDatabase)
                .build();
    }

    @Bean
    public Step populateDatabase(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("populateDatabase", jobRepository)
                .<DomainEntity, DomainEntity>chunk(10000, transactionManager)
                .reader(dummyDataReader())
                .processor(dummyDataProcessor())
                .writer(dummyDataWriter())
                .taskExecutor(taskExecutorAsyncDomain())
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public ItemReader<DomainEntity> dummyDataReader() {
        return new ItemReader<DomainEntity>() {
            private long count = 0;

            @Override
            public DomainEntity read() throws Exception {
                // Adjust this to the number of rows you need
                long limit = 1000000;
                if (count < limit) {
                    log.info("Generating data with count {}", count);
                    DomainEntity entity = new DomainEntity();
                    entity.setData1("Dummy Data " + count);
                    entity.setData2("Dummy Data " + count);
                    entity.setData3("Dummy Data " + count);
                    entity.setData4("Dummy Data " + count);
                    count++;
                    return entity;
                } else {
                    return null;
                }
            }
        };
    }

    @Bean
    public ItemProcessor<DomainEntity, DomainEntity> dummyDataProcessor() {
        return item -> item;
    }

    @Bean
    public ItemWriter<DomainEntity> dummyDataWriter() {
        log.info("Saving dummy data");
        return new ItemWriter<DomainEntity>() {
            @Override
            public void write(Chunk<? extends DomainEntity> chunk) throws Exception {
                log.info("SAVING CHUNK SIZE {}", chunk.size());
                postgresRepository.saveAll(chunk);

            }

        };
    }
}
