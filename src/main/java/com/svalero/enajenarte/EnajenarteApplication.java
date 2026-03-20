package com.svalero.enajenarte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EnajenarteApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnajenarteApplication.class, args);
	}

}
