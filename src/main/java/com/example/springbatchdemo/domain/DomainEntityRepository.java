package com.example.springbatchdemo.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomainEntityRepository extends JpaRepository<DomainEntity, Long> {

}