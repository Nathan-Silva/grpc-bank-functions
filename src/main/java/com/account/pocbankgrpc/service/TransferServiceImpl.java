package com.account.pocbankgrpc.service;

import com.account.pocbankgrpc.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransferServiceImpl extends TransferServiceGrpc.TransferServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferServiceImpl.class);
    private final AccountServiceGrpc.AccountServiceBlockingStub accountServiceStub;

    public TransferServiceImpl(AccountServiceGrpc.AccountServiceBlockingStub accountServiceStub) {
        this.accountServiceStub = accountServiceStub;
    }

    @Override
    public void transfer(TransferRequest request, StreamObserver<TransferResponse> responseObserver) {
        LOGGER.info("Recebendo solicitação de transferência de {} para {} valor {}",
                request.getFromAccountId(), request.getToAccountId(), request.getAmount());

        try {
            DebitRequest debitRequest = DebitRequest.newBuilder()
                    .setAccountId(request.getFromAccountId())
                    .setAmount(request.getAmount())
                    .build();
            accountServiceStub.debit(debitRequest);

            CreditRequest creditRequest = CreditRequest.newBuilder()
                    .setAccountId(request.getToAccountId())
                    .setAmount(request.getAmount())
                    .build();
            accountServiceStub.credit(creditRequest);

            TransferResponse response = TransferResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Transferência realizada com sucesso!")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.error("Erro na transferência: ", e);
            responseObserver.onError(
                    Status.INTERNAL.withDescription("Falha ao transferir valor.").asRuntimeException()
            );
        }
    }
}
