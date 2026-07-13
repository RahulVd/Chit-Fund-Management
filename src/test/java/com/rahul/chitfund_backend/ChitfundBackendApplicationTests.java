package com.rahul.chitfund_backend;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires a running PostgreSQL database — not available in pure unit test profile")
class ChitfundBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
