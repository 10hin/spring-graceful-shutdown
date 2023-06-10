package com.example.gracefulshutdown.client;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties={
		"baseURL=http://localhost:8080",
		"limit=10",
})
class ClientApplicationTests {

	@Test
	void contextLoads() {
	}

}
