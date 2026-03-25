package com.assignment;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.*;
import java.util.regex.*;

public class NewsRead {
    public static void main(String[] args) throws Exception {

        // Step 1: Read the .sgm file
        String filePath = "src/main/resources/reut2-009.sgm";
        StringBuilder content = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        reader.close();

        // Step 2: Connect to MongoDB
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("RawDb");
        MongoCollection<Document> collection = database.getCollection("news");

        // Step 3: Parse each <REUTERS> block
        Pattern reuterPattern = Pattern.compile("<REUTERS(.*?)</REUTERS>", Pattern.DOTALL);
        Pattern titlePattern = Pattern.compile("<TITLE>(.*?)</TITLE>", Pattern.DOTALL);
        Pattern bodyPattern = Pattern.compile("<BODY>(.*?)</BODY>", Pattern.DOTALL);

        Matcher reuterMatcher = reuterPattern.matcher(content);

        int count = 0;
        while (reuterMatcher.find()) {
            String block = reuterMatcher.group();

            String title = "";
            String body = "";

            Matcher titleMatcher = titlePattern.matcher(block);
            if (titleMatcher.find()) {
                title = titleMatcher.group(1).trim();
            }

            Matcher bodyMatcher = bodyPattern.matcher(block);
            if (bodyMatcher.find()) {
                body = bodyMatcher.group(1).trim();
            }

            // Step 4: Insert into MongoDB
            Document doc = new Document("title", title).append("body", body);
            collection.insertOne(doc);
            count++;
            System.out.println("Inserted article #" + count + ": " + title);
        }

        System.out.println("\n Done! Total articles inserted: " + count);
        mongoClient.close();
    }
}