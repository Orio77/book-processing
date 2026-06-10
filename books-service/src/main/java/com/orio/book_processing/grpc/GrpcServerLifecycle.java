package com.orio.book_processing.grpc;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionServiceV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Starts the gRPC server alongside the HTTP server. Reflection is enabled so
 * the contract can be inspected with grpcurl.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GrpcServerLifecycle implements SmartLifecycle {

    private final BooksGrpcService booksGrpcService;

    @Value("${grpc.server.port:9090}")
    private int port;

    private Server server;

    @Override
    public void start() {
        try {
            server = ServerBuilder.forPort(port)
                    .addService(booksGrpcService)
                    .addService(ProtoReflectionServiceV1.newInstance())
                    .build()
                    .start();
            log.info("gRPC server started on port {}", port);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to start gRPC server on port " + port, e);
        }
    }

    @Override
    public void stop() {
        if (server != null) {
            log.info("Shutting down gRPC server...");
            server.shutdown();
            server = null;
        }
    }

    @Override
    public boolean isRunning() {
        return server != null && !server.isShutdown();
    }
}
