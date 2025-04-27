package com.account.pocbankgrpc.service;

import com.account.pocbankgrpc.*;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class StatementServiceImpl extends StatementServiceGrpc.StatementServiceImplBase {

    @Override
    public void getStatement(Statement.StatementRequest request, StreamObserver<Statement.StatementResponse> responseObserver) {
        Statement.StatementResponse response = Statement.StatementResponse.newBuilder()
                .addTransactions("Transferência de 100 para conta 123")
                .addTransactions("Recebimento de 200 de conta 456")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
