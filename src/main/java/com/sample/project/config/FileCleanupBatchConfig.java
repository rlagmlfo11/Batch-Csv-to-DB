package com.sample.project.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.stream.Stream;

@Configuration
@EnableBatchProcessing
public class FileCleanupBatchConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Tasklet fileCleanupTasklet() {
		return (contribution, chunkContext) -> {
			Path directoryPath = Paths.get("src/main/resources/customerFiles/");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
			LocalDate aMonthAgo = LocalDate.now().minusMonths(1);

			try (Stream<Path> files = Files.walk(directoryPath)) {
				files.filter(Files::isRegularFile).forEach(path -> {
					String filename = path.getFileName().toString();
					try {
						if (filename.contains("_") && filename.contains(" ")) {
							int startIndex = filename.indexOf('_') + 1;
							int endIndex = filename.indexOf(' ');
							String dateString = filename.substring(startIndex, endIndex);
							LocalDate fileDate = LocalDate.parse(dateString, formatter);

							if (fileDate.isBefore(aMonthAgo)) {
								Files.delete(path);
								System.out.println(
										"Deleted old file based on filename date: " + filename);
							}
						}
					} catch (DateTimeParseException | IOException e) {
						System.err.println("Failed to process or delete file: " + filename);
						e.printStackTrace();
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
			return RepeatStatus.FINISHED;
		};
	}

	@Bean
	public Step fileCleanupStep() {
		return stepBuilderFactory.get("fileCleanupStep").tasklet(fileCleanupTasklet()).build();
	}

	@Bean
	public Job fileCleanupJob() {
		return jobBuilderFactory.get("fileCleanupJob").incrementer(new RunIdIncrementer())
				.start(fileCleanupStep()).build();
	}
}

//LocalDateTime currentDate = LocalDateTime.now();
//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
//String formattedDate = currentDate.format(formatter);
