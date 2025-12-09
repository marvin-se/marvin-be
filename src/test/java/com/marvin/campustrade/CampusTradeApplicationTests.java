package com.marvin.campustrade;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "jwt.secret=testsecret123")
class CampusTradeApplicationTests {

	@Test
	void contextLoads() {
	}

}
