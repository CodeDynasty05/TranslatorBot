package com.nazim;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class UserService {

    private final MongoCollection<Document> collection;

    public UserService() {
        MongoDatabase database = MongoDBClient.getClient().getDatabase("db_name");
        this.collection = database.getCollection("users");
    }

    public void createUser(int userId, String username, String language) {
        Document doc = new Document("id", userId)
                .append("username", username)
                .append("language", language);
        collection.insertOne(doc);
        System.out.println("User created.");
    }

    public void updateUserLanguage(int userId, String language) {
        Document query = new Document("id", userId);
        Document update = new Document("$set", new Document("language", language));
        collection.updateOne(query, update);
        System.out.println("User language updated.");
    }

    public void createOrUpdateUser(int userId, String username, String language) {
        Document query = new Document("id", userId);
        Document existingUser = collection.find(query).first();

        if (existingUser == null) {
            createUser(userId, username, language);
        } else {
            updateUserLanguage(userId, language);
        }
    }

    public Document getUserById(int userId) {
        return collection.find(new Document("id", userId)).first();
    }
}
