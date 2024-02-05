package com.sample.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.sample.project" })
public class CsVtoDb1Application {

	public static void main(String[] args) {
		SpringApplication.run(CsVtoDb1Application.class, args);
	}

}
