package com.sg.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class KakaoPayAssignmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(KakaoPayAssignmentApplication.class, args);
	}

}
