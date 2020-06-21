package com.sg.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SgDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(SgDataApplication.class, args);
	}
}
