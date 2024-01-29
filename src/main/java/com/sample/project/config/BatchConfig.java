package com.sample.project.config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.sample.project.dto.CustomerHistoryRepository;
import com.sample.project.dto.CustomerRepository;
import com.sample.project.entity.Customer;
import com.sample.project.entity.CustomerHistory;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerHistoryRepository customerHistoryRepository;

	@Bean
	public FlatFileItemReader<Customer> reader() {
		return new FlatFileItemReaderBuilder<Customer>().name("customerItemReader")
				.resource(new ClassPathResource("customer.csv")).delimited()
				.names(new String[] { "id", "firstname", "lastname", "email", "gender",
						"contactNumber", "country", "dob" })
				.linesToSkip(1).fieldSetMapper(new BeanWrapperFieldSetMapper<Customer>() {
					{
						setTargetType(Customer.class);
					}
				}).build();
	}

	@Bean
	public ItemProcessor<Customer, Customer> processor1() {
		return new ItemProcessor<Customer, Customer>() {
			@Override
			public Customer process(Customer customer) throws Exception {
				if ("China".equalsIgnoreCase(customer.getCountry())) {
					return customer;
				} else {
					return null;
				}
			}
		};
	}

	@Bean
	public ItemWriter<Customer> customerWriter() {
		RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
		writer.setRepository(customerRepository);
		writer.setMethodName("save");
		return writer;
	}

	@Bean
	public ItemWriter<Customer> customerHistoryWriter() {
		RepositoryItemWriter<CustomerHistory> writer = new RepositoryItemWriter<>();
		writer.setRepository(customerHistoryRepository);
		writer.setMethodName("save");
		return new ItemWriter<Customer>() {
			@Override
			public void write(List<? extends Customer> customers) throws Exception {
				List<CustomerHistory> customerHistories = customers.stream()
						.map(customer -> convertToCustomerHistory(customer))
						.collect(Collectors.toList());
				writer.write(customerHistories);
			}
		};
	}

	private CustomerHistory convertToCustomerHistory(Customer customer) {
		CustomerHistory history = new CustomerHistory();
		history.setId(customer.getId());
		history.setFirstname(customer.getFirstname());
		history.setLastname(customer.getLastname());
		history.setEmail(customer.getEmail());
		history.setGender(customer.getGender());
		history.setContactNumber(customer.getContactNumber());
		history.setCountry(customer.getCountry());
		history.setDob(customer.getDob());
		return history;
	}

	@Bean
	public ItemWriter<Customer> compositeItemWriter() {
		List<ItemWriter<? super Customer>> writers = new ArrayList<>();
		writers.add(customerWriter());
		writers.add(customerHistoryWriter());
		return new CompositeItemWriterBuilder<Customer>().delegates(writers).build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Customer, Customer>chunk(10).reader(reader())
				.processor(processor1()).writer(compositeItemWriter()).build();
	}

	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener) {
		return jobBuilderFactory.get("importCustomerJob").incrementer(new RunIdIncrementer())
				.listener(listener).flow(step1()).end().build();
	}

	@Bean
	public JobCompletionNotificationListener jobExecutionListener() {
		return new JobCompletionNotificationListener();
	}

	public static class JobCompletionNotificationListener extends JobExecutionListenerSupport {
		@Override
		public void afterJob(JobExecution jobExecution) {
			if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
				System.out.println("JOB FINISHED!");
			}
		}
	}

}
