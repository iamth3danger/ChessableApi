package com.chessable.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.boot.autoconfigure.domain.EntityScan;


@EnableScheduling
@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.chessable.worker", "com.chessable.library", "com.chessable.instance"})
@EnableJpaRepositories(basePackages = {"com.chessable.library.repository"})
@EntityScan(basePackages = {"com.chessable.library.entity", "com.chessable.instance"})
public class WorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkerApplication.class, args);
	}

}
