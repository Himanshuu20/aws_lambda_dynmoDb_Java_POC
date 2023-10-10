package org.aws.lambda.poc.model;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private int id;
    private String name;
    private double price;

//    public Product getProduct(String json) {
//        Gson gson = new Gson();
//        return gson.fromJson(json, Product.class);
//    }

    public Product(String json) {
        Gson gson = new Gson();
        Product tempProduct = gson.fromJson(json, Product.class);
        this.id = tempProduct.id;
        this.name = tempProduct.name;
        this.price = tempProduct.price;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
