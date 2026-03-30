# CSCI 5408 - Assignment 2

**Student Name:** Dhrutiben Narendrabhai Patel  
**Banner ID:** B01060910  
**Course:** CSCI 5408 - Data Management, Warehousing, and Analytics  
**Instructor:** Dr. Saurabh Dey  
**Submission Date:** March 31, 2026  

---

## Repository Links

**Primary (GitLab):**  
https://git.cs.dal.ca/dhrutiben/csci-5408.git

**Backup (GitHub):**  
https://github.com/Dhruti832/CSCI5408-Assignment-2.git

---

## Project Structure
```
B01060910_DhrutibenPatel_A2/
├── Problem1_MongoDB/
│   ├── src/main/java/com/assignment/
│   │   └── NewsRead.java
│   └── pom.xml
├── Problem2_Spark/
│   ├── src/main/java/com/assignment/
│   │   └── WordCount.java
│   └── pom.xml
├── Problem3_Sentiment/
│   ├── src/main/java/com/assignment/
│   │   └── SentimentAnalysis.java
│   ├── src/main/resources/
│   │   ├── positive-words.txt
│   │   └── negative-words.txt
│   ├── sentiment_results.csv
│   └── pom.xml
└── README.md
```

---

## Problem 1: Reuters News Data Reading & Transformation (MongoDB)

### Description
Reads Reuters news articles from reut2-009.sgm file, extracts title and 
body content using regex-based parsing inside each REUTER tag, and stores 
each article as a document in MongoDB RawDb database. A total of 1000 
articles are parsed and inserted into the news collection.

### Prerequisites
- Java 11
- MongoDB running on localhost:27017
- Maven

### How to Run
```bash
cd Problem1_MongoDB
mvn clean package
java -cp target/Problem1_MongoDB-1.0-SNAPSHOT-jar-with-dependencies.jar com.assignment.NewsRead
```

### Expected Output
- 1000 documents inserted into MongoDB RawDb > news collection
- Each document contains title and body fields as shown below:
```json
{
  "title": "ADVANCED MAGNETICS IN AGREEMENT",
  "body": "Advanced Magnetics Inc said it reached..."
}
```

---

## Problem 2: Reuters Word Count using Apache Spark

### Description
Uses Apache Spark on GCP Dataproc to perform word frequency counting 
on the reut2-009.sgm file. The program removes stop words such as 
"the", "a", "is", "and" and Reuters-specific words like "reuter" and 
"lt" before counting. The Spark job was submitted to a Single Node 
Dataproc cluster in region us-central1.

### Prerequisites
- Java 11
- Apache Spark 3.3.0
- GCP account with Dataproc cluster configured
- Maven

### How to Run
```bash
cd Problem2_Spark
mvn clean package
gcloud dataproc jobs submit spark \
  --cluster=spark-cluster \
  --region=us-central1 \
  --jar=target/Problem2_Spark-1.0-SNAPSHOT-jar-with-dependencies.jar \
  --class=com.assignment.WordCount \
  -- gs://dbms-assignment-bucket/reut2-009.sgm gs://dbms-assignment-bucket/output/
```

### Expected Output
- Word frequency output files: part-00000 and part-00001 saved in GCP bucket
- Highest frequency word: "mln" with 1283 occurrences
- Lowest frequency words: appear only once (e.g., "swissbased", "topped", "pig")

---

## Problem 3: Sentiment Analysis using BOW Model

### Description
Reads all 1000 news article titles from MongoDB RawDb database, builds 
a Bag-of-Words (BOW) for each title using a HashMap counter, compares 
each word against positive and negative word lists, calculates a sentiment 
score, and classifies each title as Positive, Negative, or Neutral. 
Results are saved to sentiment_results.csv.

### Prerequisites
- Java 11
- MongoDB running on localhost:27017 (RawDb must be populated from Problem 1 first)
- positive-words.txt and negative-words.txt inside src/main/resources/
- Maven

### How to Run
```bash
cd Problem3_Sentiment
mvn clean package
java -cp target/Problem3_Sentiment-1.0-SNAPSHOT-jar-with-dependencies.jar com.assignment.SentimentAnalysis
```

### Expected Output
- sentiment_results.csv generated with 1000 rows
- Columns: News#, Title, Matched Words, Score, Polarity
- Final Results:
  - Positive: 133 articles
  - Negative: 151 articles
  - Neutral:  712 articles

---

## References

1. Apache Software Foundation. (2024). *Apache Spark documentation*. Apache. https://spark.apache.org/docs/latest
2. Google LLC. (2024). *Dataproc documentation*. Google Cloud. https://cloud.google.com/dataproc/docs
3. Kulakowski, M. (2014). *Negative words list*. GitHub Gist. https://gist.github.com/mkulakowski2/4289441
4. MongoDB, Inc. (2024). *MongoDB Java driver documentation*. MongoDB. https://www.mongodb.com/docs/drivers/java/sync/current/
5. Oracle Corporation. (2024). *Java SE 11 API specification: java.io.BufferedReader*. Oracle. https://docs.oracle.com/en/java/api/java.base/java/io/BufferedReader.html
6. Oracle Corporation. (2024). *Java SE 11 API specification: java.io.FileWriter*. Oracle. https://docs.oracle.com/en/java/api/java.base/java/io/FileWriter.html
7. Oracle Corporation. (2024). *Java SE 11 API specification: java.util.HashMap*. Oracle. https://docs.oracle.com/en/java/api/java.base/java/util/HashMap.html
8. Oracle Corporation. (2024). *Java SE 11 API specification: java.util.regex.Pattern*. Oracle. https://docs.oracle.com/en/java/api/java.base/java/util/regex/Pattern.html
