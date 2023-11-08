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
import org.springframework.batch.item.ItemWriter;
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
public class BatchConfig {

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
				.names(new String[] { "id", "name", "mail" }).linesToSkip(1)
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Customer>() {
					{
						setTargetType(Customer.class);
					}
				}).build();
	}

	@Bean
	public CustomerItemProcessor processor() {
		return new CustomerItemProcessor();
	}

	@Bean
	public RepositoryItemWriter<Customer> writer() {
		RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
		writer.setRepository(customerRepository);
		writer.setMethodName("save");
		return writer;
	}

	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory.get("importCustomerJob").incrementer(new RunIdIncrementer())
				.listener(listener).flow(step1).end().build();
	}

	@Bean
	public Step step1(ItemWriter<Customer> writer) {
		return stepBuilderFactory.get("step1").<Customer, Customer>chunk(10).reader(reader())
				.processor(processor()).writer(writer).build();
	}

	// Processor class
	public static class CustomerItemProcessor implements ItemProcessor<Customer, Customer> {
		@Override
		public Customer process(final Customer customer) throws Exception {
			return customer; // Or any transformation you need
		}
	}

	@Bean
	public JobCompletionNotificationListener jobExecutionListener() {
		return new JobCompletionNotificationListener();
	}

	// Listener class
	public static class JobCompletionNotificationListener extends JobExecutionListenerSupport {

		@Override
		public void afterJob(JobExecution jobExecution) {
			if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
				// Log or handle completed job
				System.out.println("JOB FINISHED!");
			}
		}
	}
}
