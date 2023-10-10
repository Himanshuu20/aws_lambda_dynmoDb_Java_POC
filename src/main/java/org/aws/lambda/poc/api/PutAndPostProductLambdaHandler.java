package org.aws.lambda.poc.api;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.aws.lambda.poc.model.Product;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

public class PutAndPostProductLambdaHandler implements RequestStreamHandler {

    private String DYNAMO_TABLE = "Products";

    @Override
    @SuppressWarnings("unchecked")
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(output);
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        JSONParser parser = new JSONParser(); // this will help us to parse the request object.
        JSONObject responseObject = new JSONObject(); // we will add to this object for our api response
        JSONObject responseBody = new JSONObject(); // we will  add the item to this object.

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDB = new DynamoDB(client);

        try {
            JSONObject reqObject = (JSONObject) parser.parse(reader);

            if (reqObject.get("body") != null) {
                Product product = new Product((String) reqObject.get("body"));

                dynamoDB.getTable(DYNAMO_TABLE)
                        .putItem(new PutItemSpec().withItem(new Item()
                                .withNumber("id", product.getId())
                                .withString("name", product.getName())
                                .withNumber("price", product.getPrice())));
                responseBody.put("message", "new item created or updated.");
                responseObject.put("statusCode", 200);
            } else {
                responseBody.put("message", "No Item Found");
                responseObject.put("statusCode", 404);
            }
            responseObject.put("body", responseBody.toString());

        } catch (Exception e) {
//            context.getLogger().log("ERROR : " + e.getMessage());
            responseObject.put("statusCode", 200);
            responseObject.put("Error : ", e.getMessage());
        }
        writer.write(responseObject.toString());
        reader.close();
        writer.close();
    }
}
