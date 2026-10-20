package com.chessable.api;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.chessable.api", "com.chessable.library", "com.chessable.instance"})
@EnableJpaRepositories(basePackages = {"com.chessable.library.repository"})
@EntityScan(basePackages = {"com.chessable.library.entity", "com.chessable.instance"})
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

}
