# Intro
Localstack pollutes the JVM classpath with specific versions of AWS SDKs when executing a lambda.
This causes errors when trying to use AWS clients, such as to access DynamoDB.

# Install
First, run the `./run-localstack-docker.sh` script. 
This runs the localstack/localstack:latest docker image and binds the edge port on 4566.
Leave this script running in the background.

Although the deployment mechanism doesn't seem to matter, I used serverless for simplicity.

If you don't have it:
```
npm install -g serverless
```

Then install serverless plugins:
```
npm install 
```

We'll also want a quick deployment bucket in our localstack. Here is a quick one liner that uses the AWS CLI to create the bucket.
```
AWS_ACCESS_KEY_ID=fake AWS_SECRET_ACCESS_KEY=fake aws --endpoint-url=http://localhost:4566 s3 mb s3://example-bucket
```

# Deploy
Build the deployment file:
```
./gradlew build
```
Then deploy it with serverless:
```
sls deploy --stage=local
```

# Run
```
sls invoke -f example --stage=local
```

A nasty error will appear:
```
Exception in thread "main" java.lang.NoSuchMethodError: com.amazonaws.transform.JsonUnmarshallerContext.getCurrentToken()Lcom/fasterxml/jackson/core/JsonToken;
        at com.amazonaws.services.dynamodbv2.model.transform.ListTablesResultJsonUnmarshaller.unmarshall(ListTablesResultJsonUnmarshaller.java:39)
        at com.amazonaws.services.dynamodbv2.model.transform.ListTablesResultJsonUnmarshaller.unmarshall(ListTablesResultJsonUnmarshaller.java:29)
        at com.amazonaws.http.JsonResponseHandler.handle(JsonResponseHandler.java:118)
        at com.amazonaws.http.JsonResponseHandler.handle(JsonResponseHandler.java:43)
        at com.amazonaws.http.response.AwsResponseHandlerAdapter.handle(AwsResponseHandlerAdapter.java:69)
        at com.amazonaws.http.AmazonHttpClient$RequestExecutor.handleResponse(AmazonHttpClient.java:1627)
        at com.amazonaws.http.AmazonHttpClient$RequestExecutor.executeOneRequest(AmazonHttpClient.java:1336)
        at com.amazonaws.http.AmazonHttpClient$RequestExecutor.executeHelper(AmazonHttpClient.java:1113)
        at com.amazonaws.http.AmazonHttpClient$RequestExecutor.doExecute(AmazonHttpClient.java:770)
        at com.amazonaws.http.AmazonHttpClient$RequestExecutor.executeWithTimer(AmazonHttpClient.java:744)
        at com.amazonaws.http.AmazonHttpClient$RequestExecutor.execute(AmazonHttpClient.java:726)
        at com.amazonaws.http.AmazonHttpClient$RequestExecutor.access$500(AmazonHttpClient.java:686)
        at com.amazonaws.http.AmazonHttpClient$RequestExecutionBuilderImpl.execute(AmazonHttpClient.java:668)
        at com.amazonaws.http.AmazonHttpClient.execute(AmazonHttpClient.java:532)
        at com.amazonaws.http.AmazonHttpClient.execute(AmazonHttpClient.java:512)
        at com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient.doInvoke(AmazonDynamoDBClient.java:5110)
        at com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient.invoke(AmazonDynamoDBClient.java:5077)
        at com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient.executeListTables(AmazonDynamoDBClient.java:2465)
        at com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient.listTables(AmazonDynamoDBClient.java:2431)
        at com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient.listTables(AmazonDynamoDBClient.java:2477)
        at io.github.krrg.LambdaMain.handleRequest(LambdaMain.java:25)
        at io.github.krrg.LambdaMain.handleRequest(LambdaMain.java:11)
        at cloud.localstack.LambdaExecutor.main(LambdaExecutor.java:113)
```

Above the error, the lambda has printed the location of where certain critical classes are being loaded from:
```
AmazonDynamoDB is being loaded from (file:/tmp/localstack/zipfile.92f55cc5/lib/aws-java-sdk-dynamodb-1.11.772.jar <no signer certificates>)
JsonUnmarshallerContext is being loaded from (file:/opt/code/localstack/localstack/infra/localstack-utils-fat.jar <no signer certificates>)
```

What we can see is that `com.amazonaws.services.dynamodbv2.AmazonDynamoDB` is loaded from the user-specified zip, but that `com.amazonaws.transform.JsonUnmarshallerContext` is being loaded from the `localstack-utils-fat.jar`.












