package com.kumuditha.hitl;

/*
 * File: HitlBackendApplication.java
 *
 * Description:
 * Spring Boot application entrypoint for the HITL backend.
 *
 * Responsibilities:
 * - Bootstraps Spring Boot auto-configuration and component scanning.
 * - Starts the embedded server and application context.
 *
 * Used in:
 * - Application startup (local development, tests, and packaged deployment).
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HitlBackendApplication {

	/**
	 * Starts the Spring Boot application.
	 *
	 * @param args command-line arguments forwarded to Spring Boot
	 */
	public static void main(String[] args) {
		SpringApplication.run(HitlBackendApplication.class, args);
	}
}
