package com.kumuditha.hitl;

/*
 * File: HitlBackendApplicationTests.java
 *
 * Description:
 * Basic smoke tests for the Spring Boot application.
 *
 * Responsibilities:
 * - Verifies that the Spring application context loads successfully.
 *
 * Used in:
 * - CI/local test runs as a quick configuration sanity check.
 */

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HitlBackendApplicationTests {

	/**
	 * Ensures the Spring application context starts without errors.
	 */
	@Test
	void contextLoads() {
	}

}
