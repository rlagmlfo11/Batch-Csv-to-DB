package com.sample.project;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CsVtoDb1Application {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job fileCleanupJob; // Ensure this matches the bean name of your cleanup job

	public static void main(String[] args) {
		SpringApplication.run(CsVtoDb1Application.class, args);

	}

	@Bean
	public CommandLineRunner runBatchJob() {
		return args -> {
			jobLauncher.run(fileCleanupJob,
					new JobParametersBuilder().addDate("launchDate", new Date()).toJobParameters());
		};
	}

}
