package org.example.processingservice.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class LambdaService {

    private final LambdaClient lambdaClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public LambdaService(LambdaClient lambdaClient) {
        this.lambdaClient = lambdaClient;
    }

    public String invokeLogger(String message) throws Exception {

        String payload = mapper.writeValueAsString(
                Map.of("message", message)
        );

        InvokeRequest request = InvokeRequest.builder()
                .functionName("spring-poc-logger")
                .invocationType(InvocationType.EVENT)
                .payload(SdkBytes.fromUtf8String(payload))
                .build();

        InvokeResponse response = lambdaClient.invoke(request);

        System.out.println("Lambda invoked with status code: " + response.statusCode());

        return response.payload().asUtf8String();
    }
}
