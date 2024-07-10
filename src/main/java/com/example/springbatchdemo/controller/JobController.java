package com.example.springbatchdemo.controller;

import com.example.springbatchdemo.service.JobService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@AllArgsConstructor
public class JobController {

    private final JobService jobService;


    @GetMapping("/run-data-population-job")
    public ResponseEntity<HttpStatus> runDataPopulationJob() {
        jobService.populateDatabaseJobTrigger();
        return ResponseEntity.ok().build();
    }


    @GetMapping("/run-elastic-indexing-job")
    public ResponseEntity<HttpStatus> runElasticIndexingJob() {
        jobService.indexDomainEntitiesToElasticsearch();
        return ResponseEntity.ok().build();

    }


}

