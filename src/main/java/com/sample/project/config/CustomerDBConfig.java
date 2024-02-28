//package com.sample.project.config;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//import org.springframework.batch.core.BatchStatus;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.core.listener.JobExecutionListenerSupport;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.batch.item.data.RepositoryItemWriter;
//import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.FlatFileParseException;
//import org.springframework.batch.item.file.LineMapper;
//import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
//import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Component;
//
//import com.sample.project.dto.CustomerRepository;
//import com.sample.project.entity.Customer;
//
//@Configuration
//@EnableBatchProcessing
//public class CustomerDBConfig {
//
//	LocalDateTime currentDate = LocalDateTime.now();
//	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
//	String formattedDate = currentDate.format(formatter);
//
//	@Autowired
//	public JobBuilderFactory jobBuilderFactory;
//
//	@Autowired
//	public StepBuilderFactory stepBuilderFactory;
//
//	@Autowired
//	private CustomerRepository customerRepository;
//
////	@Bean
////	public FlatFileItemReader<Customer> reader() {
////		return new FlatFileItemReaderBuilder<Customer>().name("customerItemReader")
////				.resource(new ClassPathResource("customer.csv"))
////				.delimited()
////				.names(new String[] { "id", "firstname", "lastname", "email", "gender" })
////				.linesToSkip(1)
////				.fieldSetMapper(new BeanWrapperFieldSetMapper<Customer>() {
////					{
////						setTargetType(Customer.class);
////					}
////				}).build();
////	}
//
//	@Bean
//	public FlatFileItemReader<Customer> reader() {
//		FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
//		reader.setResource(new ClassPathResource("customer.csv"));
//		reader.setLinesToSkip(1);
//
//		reader.setLineMapper(new LineMapper<Customer>() {
//
//			@Override
//			public Customer mapLine(String line, int lineNumber) throws Exception {
//				String[] tokens = line.split(",");
//				if (tokens.length < 5) {
//					throw new FlatFileParseException("Not enough data in line", line, lineNumber);
//				}
//				Customer customer = new Customer();
//				customer.setId(Long.parseLong(tokens[0]));
//				customer.setFirstname(tokens[1]);
//				customer.setLastname(tokens[2]);
//				customer.setEmail(tokens[3]);
//				customer.setGender(tokens[4]);
//				return customer;
//			}
//		});
//
//		return reader;
//	}
//
//	@Bean
//	public ItemProcessor<Customer, Customer> processor1() {
//		return new ItemProcessor<Customer, Customer>() {
//			@Override
//			public Customer process(Customer customer) throws Exception {
//				return customer;
//			}
//		};
//	}
//
//	@Bean
//	public RepositoryItemWriter<Customer> customerWriter() {
//		RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
//		writer.setRepository(customerRepository);
//		writer.setMethodName("save");
//		return writer;
//	}
//
//	@Bean
//	public Step step1() {
//		return stepBuilderFactory.get("step1").<Customer, Customer>chunk(10).reader(reader())
//				.processor(processor1()).writer(customerWriter()).build();
//	}
//
//	@Bean
//	public Job importUserJob() {
//		Job job = jobBuilderFactory.get("importCustomerJob").incrementer(new RunIdIncrementer())
//				.flow(step1()).end().build();
//
//		// Consider placing cleanup logic here if you want to ensure database is cleaned
//		// before job execution
//		return job;
//	};
//
//}
////	@Component
////	public static class JobCompletionNotificationListener extends JobExecutionListenerSupport {
////		@Autowired
////		private CustomerRepository customerRepository;
////
////		@Override
////		public void beforeJob(JobExecution jobExecution) {
////			super.beforeJob(jobExecution);
////			// Optional: Cleanup logic here if you prefer cleanup before each job starts
////		}
//
////		@Override
////		public void afterJob(JobExecution jobExecution) {
////			if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
////				System.out.println("Customer database finished. Checking for data cleanup...");
////
////				// Example cleanup logic
////				String oldestReceivedDate = customerRepository.findOldestReceivedDate();
////				if (oldestReceivedDate != null) {
////					// Assuming you have a way to check if there are more than two distinct dates
////					List<String> distinctDates = customerRepository.findDistinctReceivedDates();
////					if (distinctDates.size() > 2) {
////						customerRepository.deleteByReceivedDate(oldestReceivedDate);
////						System.out.println("Old data from " + oldestReceivedDate + " deleted.");
////					}
////				}
////			}
////		}
////}
//
////	@Bean
////	public Job importUserJob(JobCompletionNotificationListener listener) {
////		return jobBuilderFactory.get("importCustomerJob").incrementer(new RunIdIncrementer())
////				.listener(listener).flow(step1()).end().build();
////	}
////
////	@Bean
////	public JobCompletionNotificationListener jobExecutionListener() {
////		return new JobCompletionNotificationListener();
////	}
////
////	public static class JobCompletionNotificationListener extends JobExecutionListenerSupport {
////		@Override
////		public void afterJob(JobExecution jobExecution) {
////			if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
////				System.out.println("customer database finished");
////			}
////		}
////	}
