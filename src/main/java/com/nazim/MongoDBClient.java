package com.nazim;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoDBClient {
    private static final String DB_URI = System.getenv("DB_URI");
    private static final MongoClient mongoClient;

    static {
        java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(java.util.logging.Level.OFF);
        MongoClientURI uri = new MongoClientURI(DB_URI);
        mongoClient = new MongoClient(uri);
    }

    public static MongoClient getClient() {
        return mongoClient;
    }
}
