package com.example.springbatchdemo.elasticsearch.repository;

import com.example.springbatchdemo.elasticsearch.models.ElasticsearchEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticsearchEntityRepository extends ElasticsearchRepository<ElasticsearchEntity, String> {
}
