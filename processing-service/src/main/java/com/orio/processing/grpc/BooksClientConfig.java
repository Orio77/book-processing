package com.orio.processing.grpc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.orio.proto.books.BooksServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Configuration
public class BooksClientConfig {

    @Bean(destroyMethod = "shutdown")
    ManagedChannel booksChannel(@Value("${books.grpc.host:localhost}") String host,
            @Value("${books.grpc.port:9090}") int port) {
        return ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
    }

    @Bean
    BooksServiceGrpc.BooksServiceBlockingStub booksStub(ManagedChannel booksChannel) {
        return BooksServiceGrpc.newBlockingStub(booksChannel);
    }
}
