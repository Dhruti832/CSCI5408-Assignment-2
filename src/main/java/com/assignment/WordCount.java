package com.assignment;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;
import java.util.Comparator;

public class WordCount {
    public static void main(String[] args) {

        // Step 1: Configure Spark
        SparkConf conf = new SparkConf().setAppName("WordCount");
        JavaSparkContext sc = new JavaSparkContext(conf);

        // Step 2: Load the SGM file
        String inputFile = args[0];
        JavaRDD<String> lines = sc.textFile(inputFile);

        // Step 3: Define stop words to remove
        List<String> stopWords = Arrays.asList(
            "the","a","an","and","or","in","of","to",
            "is","it","was","for","on","are","as","at",
            "be","by","from","that","this","with","has",
            "have","had","not","but","they","he","she",
            "we","you","i","its","his","her","their",
            "said","will","would","could","should","lt",
            "gt","amp","reuter","reuters"
        );

        // Step 4: Count word frequencies
        JavaPairRDD<String, Integer> wordCounts = lines
            .flatMap(line -> Arrays.asList(
                line.toLowerCase()
                    .replaceAll("[^a-z\\s]", "")
                    .split("\\s+"))
                .iterator())
            .filter(word -> !word.isEmpty() 
                && !stopWords.contains(word))
            .mapToPair(word -> new Tuple2<>(word, 1))
            .reduceByKey((a, b) -> a + b);

        // Step 5: Sort by frequency descending
        JavaPairRDD<String, Integer> sorted = wordCounts
            .mapToPair(x -> new Tuple2<>(x._2, x._1))
            .sortByKey(false)
            .mapToPair(x -> new Tuple2<>(x._2, x._1));

        // Step 6: Save output
        sorted.saveAsTextFile(args[1]);

        // Step 7: Print top 10 highest frequency words
        System.out.println("=== TOP 10 HIGHEST FREQUENCY WORDS ===");
        sorted.take(10).forEach(t -> System.out.println(t._1 + " : " + t._2));

        // Step 8: Print top 10 lowest frequency words
        JavaPairRDD<String, Integer> sortedAsc = wordCounts
            .mapToPair(x -> new Tuple2<>(x._2, x._1))
            .sortByKey(true)
            .mapToPair(x -> new Tuple2<>(x._2, x._1));

        System.out.println("=== TOP 10 LOWEST FREQUENCY WORDS ===");
        sortedAsc.take(10).forEach(t -> System.out.println(t._1 + " : " + t._2));

        sc.close();
    }
}