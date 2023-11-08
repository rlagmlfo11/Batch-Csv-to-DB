package com.sample.project.dto;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sample.project.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
