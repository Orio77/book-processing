package com.orio.book_processing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// grpc.server.port=0 -> ephemeral port so tests don't clash with a running instance
@SpringBootTest(properties = "grpc.server.port=0")
class BooksServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
