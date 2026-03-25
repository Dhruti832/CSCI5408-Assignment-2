package com.assignment;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.*;
import java.util.*;

public class SentimentAnalysis {

    public static void main(String[] args) throws Exception {

        // Step 1: Load positive words
        Set<String> positiveWords = loadWords("src/main/resources/positive-words.txt");

        // Step 2: Load negative words
        Set<String> negativeWords = loadWords("src/main/resources/negative-words.txt");

        System.out.println("Positive words loaded: " + positiveWords.size());
        System.out.println("Negative words loaded: " + negativeWords.size());

        // Step 3: Connect to MongoDB and get titles
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("RawDb");
        MongoCollection<Document> collection = database.getCollection("news");

        // Step 4: Create output CSV file
        FileWriter fw = new FileWriter("sentiment_results.csv");
        fw.write("News#,Title,Matched Words,"+ "Score,Polarity\n");

        int newsNum = 1;

        // Step 5: Process each article title
        for (Document doc : collection.find()) {
            String title = doc.getString("title");

            if (title == null || title.isEmpty()) {
                newsNum++;
                continue;
            }

            // Step 6: Build bag of words
            Map<String, Integer> bow = new HashMap<>();
            String[] words = title.toLowerCase().replaceAll("[^a-z ]", "").split("\\s+");

            for (String word : words) {
                if (!word.isEmpty()) {
                    bow.put(word, bow.getOrDefault(word, 0) + 1);
                }
            }

            // Step 7: Compare with positive/negative words
            int score = 0;
            List<String> matchedWords = new ArrayList<>();

            for (String word : bow.keySet()) {
                if (positiveWords.contains(word)) {
                    score += bow.get(word);
                    matchedWords.add(word + "(+)");
                }
                if (negativeWords.contains(word)) {
                    score -= bow.get(word);
                    matchedWords.add(word + "(-)");
                }
            }

            // Step 8: Determine polarity
            String polarity;
            if (score > 0)      polarity = "Positive";
            else if (score < 0) polarity = "Negative";
            else                polarity = "Neutral";

            // Step 9: Write to CSV
            String matched = matchedWords.isEmpty() ? "none" : String.join(" | ", matchedWords);

            fw.write(newsNum + ",\""+ title.replace("\"", "'") + "\",\""+ matched + "\"," + score + "," + polarity + "\n");

            System.out.println("Processed #" + newsNum + ": " + polarity + " → " + title);

            newsNum++;
        }

        fw.close();
        mongoClient.close();

        System.out.println("\n Done!");
        System.out.println("Results saved to: "+ "sentiment_results.csv");
    }

    // Method to load words from file into a Set
    private static Set<String> loadWords(String filePath) throws Exception {
        Set<String> words = new HashSet<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim().toLowerCase();
            if (!line.isEmpty() && !line.startsWith(";")&& !line.startsWith("#")) {
                words.add(line);
            }
        }
        reader.close();
        return words;
    }
}