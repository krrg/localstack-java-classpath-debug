package io.github.krrg;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

public class LambdaMain implements RequestHandler<Map<String, String>, String> {

    @Override
    public String handleRequest(Map<String, String> input, Context context) {

        System.err.println("AmazonDynamoDB is being loaded from "
                + AmazonDynamoDB.class.getProtectionDomain().getCodeSource());
        System.err.println("JsonUnmarshallerContext is being loaded from "
                + com.amazonaws.transform.JsonUnmarshallerContext.class.getProtectionDomain().getCodeSource());

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                "http://localhost:4566",
                                "us-east-1"
                        )
                )
                .build();

        client.listTables().getTableNames().forEach(tableName -> {
            System.out.println("Found Dynamo table: " + tableName);
        });

        return "";
    }
}
