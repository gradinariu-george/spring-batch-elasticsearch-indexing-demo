package com.example.springbatchdemo.elasticsearch.models;

import com.example.springbatchdemo.domain.DomainEntity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

@Document(indexName = "your_index")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ElasticsearchEntity {
    @Id
    private UUID id;
    private String data1;
    private String data2;
    private String data3;
    private String data4;


    public ElasticsearchEntity(DomainEntity domainEntity) {
        this.id = domainEntity.getId();
        this.data1 = domainEntity.getData1();
        this.data2 = domainEntity.getData2();
        this.data2 = domainEntity.getData3();
        this.data2 = domainEntity.getData4();
    }
}
