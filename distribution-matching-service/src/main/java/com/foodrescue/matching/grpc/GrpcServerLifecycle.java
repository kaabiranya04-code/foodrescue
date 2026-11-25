package com.foodrescue.matching.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/** Démarre et arrête le serveur gRPC avec l'application Spring (port 9090 par défaut). */
@Component
public class GrpcServerLifecycle implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(GrpcServerLifecycle.class);

    private final MatchingGrpcService service;
    private final int port;
    private Server server;

    public GrpcServerLifecycle(MatchingGrpcService service,
                               @Value("${grpc.server.port:9090}") int port) {
        this.service = service;
        this.port = port;
    }

    @Override
    public void start() {
        try {
            server = ServerBuilder.forPort(port)
                    .addService(service)
                    .addService(ProtoReflectionService.newInstance()) // permet grpcurl sans fichier .proto
                    .build()
                    .start();
            log.info("Serveur gRPC demarre sur le port {}", server.getPort());
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de demarrer le serveur gRPC sur le port " + port, e);
        }
    }

    @Override
    public void stop() {
        if (server == null) {
            return;
        }
        server.shutdown();
        try {
            if (!server.awaitTermination(5, TimeUnit.SECONDS)) {
                server.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            server.shutdownNow();
        }
    }

    @Override
    public boolean isRunning() {
        return server != null && !server.isShutdown();
    }

    public int getPort() {
        return server == null ? port : server.getPort();
    }
}
