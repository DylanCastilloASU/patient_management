package com.pm.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    // LOCAL VERSION: localhost:9001/BillingService/createBillingAccount
    // CLOUD(AWS) VERSION: aws.grpc:12345/BillingService/createBillingAccount
    public BillingServiceGrpcClient(
            @Value("${billing.service.address:localhost}") String serverAddress,
            @Value("${billing.service.grpc.port:9001}") int port
        ) {

        log.info("Billing service grpc server started at {}:{}", serverAddress, port);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, port).usePlaintext().build();

        blockingStub = BillingServiceGrpc.newBlockingStub(channel);
    }

    public BillingResponse createBillingAccount(
            String patientId,
            String firstName,
            String lastName,
            String email
    ) {
        //creating the info required (basically creating a JSON file with the needed info the api needs)
        BillingRequest billingRequest = BillingRequest.newBuilder()
                .setPatientId(patientId)
                .setFirstname(firstName)
                .setLastname(lastName)
                .setEmail(email)
                .build();
        //sending the request (so like a POST request in REST API)
        BillingResponse response = blockingStub.createBillingAccount(billingRequest);
        //display the info we got from billing service
        log.info("Received Response from Billing-Service via GRPC: {}", response);
        return response;
    }
}
