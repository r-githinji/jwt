package com.auth.example.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest(classes = {JwtApplication.class})
public class JwtApplicationTests {

	private static final Logger logger = LoggerFactory.getLogger(JwtApplicationTests.class);
	
	@Test
	void contextLoads() {
		logger.info("TESTS SUCCEEDED!");
	}
}
