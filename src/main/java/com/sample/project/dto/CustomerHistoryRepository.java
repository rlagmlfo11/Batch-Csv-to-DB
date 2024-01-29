package com.sample.project.dto;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sample.project.entity.Customer;
import com.sample.project.entity.CustomerHistory;

public interface CustomerHistoryRepository extends JpaRepository<CustomerHistory, Long> {

}
