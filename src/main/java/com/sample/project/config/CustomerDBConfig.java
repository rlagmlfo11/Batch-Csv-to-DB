package com.sample.project.config;

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
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.sample.project.dto.CustomerRepository;
import com.sample.project.entity.Customer;

@Configuration
@EnableBatchProcessing
public class CustomerDBConfig {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	private CustomerRepository customerRepository;

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
	public RepositoryItemWriter<Customer> customerWriter() {
		RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
		writer.setRepository(customerRepository);
		writer.setMethodName("save");
		return writer;
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Customer, Customer>chunk(10).reader(reader())
				.processor(processor1()).writer(customerWriter()).build();
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
				System.out.println("customer database finished");
			}
		}
	}

}