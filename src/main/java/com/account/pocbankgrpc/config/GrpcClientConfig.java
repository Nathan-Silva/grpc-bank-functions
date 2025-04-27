package com.account.pocbankgrpc.config;

import com.account.pocbankgrpc.AccountServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Value("${grpc.server.host:localhost}")
    private String grpcHost;

    @Value("${grpc.server.port:6565}")
    private int grpcPort;

    @Bean
    public AccountServiceGrpc.AccountServiceBlockingStub accountServiceStub() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(grpcHost, grpcPort)
                .usePlaintext()
                .build();
        return AccountServiceGrpc.newBlockingStub(channel);
    }
}
