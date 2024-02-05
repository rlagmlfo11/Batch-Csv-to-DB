package com.sample.project.deleteCSV;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FileCleanupTask {
	private static final String DIRECTORY_PATH = "src/main/resources/customerFiles";
	private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter
			.ofPattern("yyyyMMdd");

	@Scheduled(cron = "0 0 1 * * ?") // Runs daily at 1 AM
	public void deleteOldCsvFiles() {
		LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
		File folder = new File(DIRECTORY_PATH);
		File[] listOfFiles = folder.listFiles();

		if (listOfFiles != null) {
			for (File file : listOfFiles) {
				String fileName = file.getName();
				if (fileName.startsWith("Customer_")) {
					try {
						String dateString = fileName.substring(9, 17); // Extract date from filename
						LocalDate fileDate = LocalDate.parse(dateString, FILE_DATE_FORMAT);
						if (fileDate.isBefore(threeMonthsAgo)) {
							boolean deleted = file.delete();
							if (deleted) {
								System.out.println(fileName + " was deleted.");
							} else {
								System.out.println("Could not delete " + fileName);
							}
						}
					} catch (DateTimeParseException e) {
						System.out.println("Could not parse date for " + fileName);
					}
				}
			}
		}
	}
}