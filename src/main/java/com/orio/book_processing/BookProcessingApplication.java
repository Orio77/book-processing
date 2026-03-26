package com.orio.book_processing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class BookProcessingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookProcessingApplication.class, args);
    }

}
