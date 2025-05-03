package com.nazim;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoDBClient {
    private static final MongoClient mongoClient;

    static {
        java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(java.util.logging.Level.OFF);
        MongoClientURI uri = new MongoClientURI("mongodb+srv://Nazim:4AW9rEXXDZy6AUaZ@nazim.234oayu.mongodb.net/?retryWrites=true&w=majority&appName=Nazim");
        mongoClient = new MongoClient(uri);
    }

    public static MongoClient getClient() {
        return mongoClient;
    }
}
