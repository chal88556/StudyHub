package com.example.WebChat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "Students"; // Your MongoDB database name
    }

    @Override
    public MongoClient mongoClient() {
        // Adjust URI if needed
        return MongoClients.create("mongodb://localhost:27017");
    }
}
