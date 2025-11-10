package com.dev.photoshare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PhotoshareApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotoshareApplication.class, args);
	}

}
