package com.account.pocbankgrpc.service;

import com.account.pocbankgrpc.*;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountServiceImpl extends AccountServiceGrpc.AccountServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final ConcurrentHashMap<String, Double> accounts = new ConcurrentHashMap<>();

    @Override
    public void getBalance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        String accountId = request.getAccountId();
        double balance = accounts.getOrDefault(accountId, 0.0);
        LOGGER.info("Consulta de saldo para a conta {}: Saldo = {}", accountId, balance);

        BalanceResponse response = BalanceResponse.newBuilder()
                .setBalance(balance)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void debit(DebitRequest request, StreamObserver<TransactionResponse> responseObserver) {
        String accountId = request.getAccountId();
        double amount = request.getAmount();

        double currentBalance = accounts.getOrDefault(accountId, 0.0);
        LOGGER.info("Tentando debitar: Conta = {}, Valor = {}, Saldo atual = {}", accountId, amount, currentBalance);

        if (currentBalance >= amount) {
            accounts.put(accountId, currentBalance - amount);
            LOGGER.info("Débito realizado com sucesso: Conta = {}, Novo saldo = {}", accountId, currentBalance - amount);

            responseObserver.onNext(TransactionResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Débito realizado com sucesso")
                    .build());
        } else {
            LOGGER.warn("Falha no débito: Conta = {}, Saldo insuficiente. Saldo atual = {}, Valor solicitado = {}",
                    accountId, currentBalance, amount);
            responseObserver.onNext(TransactionResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Saldo insuficiente")
                    .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void credit(CreditRequest request, StreamObserver<TransactionResponse> responseObserver) {
        String accountId = request.getAccountId();
        double amount = request.getAmount();

        LOGGER.info("Tentando creditar: Conta = {}, Valor = {}", accountId, amount);
        double newBalance = accounts.merge(accountId, amount, Double::sum);

        LOGGER.info("Crédito realizado com sucesso: Conta = {}, Novo saldo = {}", accountId, newBalance);

        responseObserver.onNext(TransactionResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Crédito realizado com sucesso")
                .build());
        responseObserver.onCompleted();
    }
}
