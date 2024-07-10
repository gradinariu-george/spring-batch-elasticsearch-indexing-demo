package com.example.springbatchdemo.jobs;

import com.example.springbatchdemo.config.JobLoggerListener;
import com.example.springbatchdemo.domain.DomainEntity;
import com.example.springbatchdemo.elasticsearch.models.ElasticsearchEntity;
import com.example.springbatchdemo.elasticsearch.repository.ElasticsearchEntityRepository;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ElasticSearchIndexing {


    private final ElasticsearchEntityRepository elasticsearchRepository;
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    public ElasticSearchIndexing(ElasticsearchEntityRepository elasticsearchRepository) {
        this.elasticsearchRepository = elasticsearchRepository;
    }

    @Bean
    public Job indexingElasticJob(JobRepository jobRepository, Step elasticsearchIndexing) {
        return new JobBuilder("indexingElasticJob", jobRepository)
                .listener(new JobLoggerListener())
                .start(elasticsearchIndexing)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(10); // Number of threads
        return taskExecutor;
    }

    @Bean
    public Step elasticsearchIndexing(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("elasticsearchIndexing", jobRepository)
                .<DomainEntity, ElasticsearchEntity>chunk(100000, transactionManager)
                .reader(reader())
                .processor(processor())
                .taskExecutor(taskExecutor())
                .writer(writer())
                .build();
    }

    @Bean
    public ItemReader<DomainEntity> reader() {
        return new JpaPagingItemReaderBuilder<DomainEntity>()
                .name("domainDataReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT p FROM DomainEntity p order by id")
                .pageSize(100000)
                .build();
    }

    @Bean
    public ItemProcessor<DomainEntity, ElasticsearchEntity> processor() {
        return ElasticsearchEntity::new;
    }

    @Bean
    public ItemWriter<ElasticsearchEntity> writer() {
        return elasticsearchRepository::saveAll;
    }
}
