package org.aws.lambda.poc.api;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

public class DeleteProductLambdaHandler implements RequestStreamHandler {
    private String DYNAMO_TABLE = "Products";

    @SuppressWarnings("unchecked")
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(output);
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        JSONParser parser = new JSONParser(); // this will help us to parse the request object.
        JSONObject responseObject = new JSONObject(); // we will add to this object for our api response
        JSONObject responseBody = new JSONObject(); // we will  add the item to this object.

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDB = new DynamoDB(client);

        int id;

        try {
            JSONObject reqObject = (JSONObject) parser.parse(reader);

            if (reqObject.get("pathParameters") != null) {
                JSONObject pathParam = (JSONObject) reqObject.get("pathParameters");
                if (pathParam.get("id") != null) {
                    id = Integer.parseInt((String) pathParam.get("id"));
                    dynamoDB.getTable(DYNAMO_TABLE).deleteItem("id", id);
                }
            } else if (reqObject.get("queryStringParameters") != null) {
                JSONObject queryParam = (JSONObject) reqObject.get("queryStringParameters");
                if (queryParam.get("id") != null) {
                    id = Integer.parseInt((String) queryParam.get("id"));
                    dynamoDB.getTable(DYNAMO_TABLE).deleteItem("id", id);
                }
            }

            responseBody.put("message", "Item deleted");
            responseObject.put("statusCode", 200);
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
