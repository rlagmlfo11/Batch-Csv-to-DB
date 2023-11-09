package com.sample.project.config;

import org.springframework.batch.item.ItemProcessor;

import com.sample.project.entity.Customer;

// Processor class
public class CustomerItemProcessor implements ItemProcessor<Customer, Customer> {
	@Override
	public Customer process(final Customer customer) throws Exception {
		return customer; // Or any transformation you need
	}
}
