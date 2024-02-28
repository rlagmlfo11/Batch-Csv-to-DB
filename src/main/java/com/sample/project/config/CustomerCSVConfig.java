//package com.sample.project.config;
//
//import java.io.IOException;
//import java.io.Writer;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
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
//import org.springframework.batch.item.file.FlatFileHeaderCallback;
//import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.FlatFileItemWriter;
//import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
//import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
//import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
//import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.scheduling.annotation.EnableScheduling;
//
//import com.sample.project.entity.Customer;
//
//@Configuration
//@EnableBatchProcessing
//@EnableScheduling
//public class CustomerCSVConfig {
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
//	@Bean
//	public FlatFileItemReader<Customer> CSVreader() {
//		return new FlatFileItemReaderBuilder<Customer>().name("customerItemReader")
//				.resource(new ClassPathResource("customer.csv")).delimited()
//				.names(new String[] { "id", "firstname", "lastname", "email", "gender",
//						"contactNumber", "country", "dob" })
//				.linesToSkip(1).fieldSetMapper(new BeanWrapperFieldSetMapper<Customer>() {
//					{
//						setTargetType(Customer.class);
//					}
//				}).build();
//	}
//
//	@Bean
//	public ItemProcessor<Customer, Customer> itemprocessor1() {
//		return new ItemProcessor<Customer, Customer>() {
//			@Override
//			public Customer process(Customer customer) throws Exception {
//				return customer;
//			}
//		};
//
//	}
//
//	@Bean(name = "resultTableCsvWriter")
//	public FlatFileItemWriter<Customer> resultTableCsvWriter() {
//
//		FlatFileItemWriter<Customer> writer = new FlatFileItemWriter<>();
//		writer.setResource(new FileSystemResource(
//				"src/main/resources/customerFiles/Customer_" + formattedDate + ".csv"));
//		writer.setAppendAllowed(true);
//		writer.setHeaderCallback(new FlatFileHeaderCallback() {
//			@Override
//			public void writeHeader(Writer headerWriter) throws IOException {
//				headerWriter.write("ID,이름,성,메일,성별,연락처,국가,생일");
//			}
//		});
//		DelimitedLineAggregator<Customer> lineAggregator = new DelimitedLineAggregator<>();
//		lineAggregator.setDelimiter(",");
//
//		BeanWrapperFieldExtractor<Customer> fieldExtractor = new BeanWrapperFieldExtractor<>();
//		fieldExtractor.setNames(new String[] { "id", "firstname", "lastname", "email", "gender",
//				"contactNumber", "country", "dob" });
//
//		lineAggregator.setFieldExtractor(fieldExtractor);
//		writer.setLineAggregator(lineAggregator);
//
//		return writer;
//	}
//
//	@Bean
//	public Step step11() {
//		return stepBuilderFactory.get("step1").<Customer, Customer>chunk(10).reader(CSVreader())
//				.processor(itemprocessor1()).writer(resultTableCsvWriter()).build();
//	}
//
//	@Bean
//	public Job customerCSVjob(JobCompletionNotificationListener listener) {
//		return jobBuilderFactory.get("importCustomerJob").incrementer(new RunIdIncrementer())
//				.listener(listener).flow(step11()).end().build();
//	}
//
//	@Bean
//	public JobCompletionNotificationListener jobExecutionListener1() {
//		return new JobCompletionNotificationListener();
//	}
//
//	public static class JobCompletionNotificationListener extends JobExecutionListenerSupport {
//		@Override
//		public void afterJob(JobExecution jobExecution) {
//			if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
//				System.out.println("customer accumulates finished");
//			}
//		}
//	}
//
//}
