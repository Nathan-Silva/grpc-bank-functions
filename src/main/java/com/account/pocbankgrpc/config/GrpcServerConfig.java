package com.account.pocbankgrpc.config;

import com.account.pocbankgrpc.service.AccountServiceImpl;
import com.account.pocbankgrpc.service.StatementServiceImpl;
import com.account.pocbankgrpc.service.TransferServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcServerConfig {

    private final AccountServiceImpl accountService;
    private final TransferServiceImpl transferService;
    private final StatementServiceImpl statementService;
    private Server server;

    public GrpcServerConfig(AccountServiceImpl accountService,
                            TransferServiceImpl transferService,
                            StatementServiceImpl statementService) {
        this.accountService = accountService;
        this.transferService = transferService;
        this.statementService = statementService;
    }

    @PostConstruct
    public void startServer() throws Exception {
        server = ServerBuilder
                .forPort(6565)
                .addService(accountService)
                .addService(transferService)
                .addService(statementService)
                .build()
                .start();
        System.out.println("gRPC Server started on port 6565");
    }

    @PreDestroy
    public void stopServer() throws Exception {
        if (server != null) {
            server.shutdown();
            System.out.println("gRPC Server stopped");
        }
    }
}
