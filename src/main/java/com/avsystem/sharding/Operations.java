package com.avsystem.sharding;

import com.google.common.base.Throwables;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang.RandomStringUtils;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class Operations {
    private MongoClient mongo = new MongoClient(Collections.singletonList(
            new ServerAddress("ghikpad", 27017)));
    private String dbName = getDbName();
    private MongoDatabase db = mongo.getDatabase(dbName);
    private MongoCollection<Document> coll = db.getCollection("tweets");

    private long lastReport = 0;
    private long count = 0;

    private void reportProgress(int amount) {
        count += amount;
        long now = System.currentTimeMillis();
        if(now-lastReport >= 1000) {
            System.out.println(count);
            count = 0;
            lastReport = now;
        }
    }

    private String getDbName() {
        try {
            Properties props = new Properties();
            props.load(getClass().getResourceAsStream("/config.properties"));
            return props.getProperty("dbName");
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private List<String> randomTags(Random r) {
        int ntags = r.nextInt(6);
        List<String> result = new ArrayList<>(ntags);
        for (int i = 0; i < ntags; i++) {
            result.add(RandomStringUtils.randomAlphabetic(3 + r.nextInt(8)));
        }
        return result;
    }

    public void insertTweets() throws Exception {
        List<String> names = Names.getNames();
        List<String> countries = Countries.getCountries();

        Random r = ThreadLocalRandom.current();
        while (true) {
            String author = names.get(r.nextInt(names.size())) + r.nextInt(10);
            String country = countries.get(r.nextInt(countries.size()));
            String content = RandomStringUtils.random(40 + r.nextInt(100));
            coll.insertOne(
                    new Document("date", new Date())
                            .append("author", author)
                            .append("country", country)
                            .append("content", content)
                            .append("tags", randomTags(r))
            );
            reportProgress(1);
            Thread.sleep(1);
        }
    }

    public void countTweets() throws Exception {
        String author = coll.find().first().getString("author");
        while (true) {
            coll.count(new Document("author", author));
            reportProgress(1);
            Thread.sleep(10);
        }
    }

    public void getUserTweets() throws Exception {
        String author = coll.find().first().getString("author");
        Consumer<Document> doNothing = (d -> {
        });
        while (true) {
            coll.find(new Document("author", author))
                    .sort(new Document("date", -1))
                    .limit(1000)
                    .forEach(doNothing);
            reportProgress(1);
            Thread.sleep(10);
        }
    }

    public void getCountryTweets() throws Exception {
        String author = coll.find().first().getString("country");
        Consumer<Document> doNothing = (d -> {
        });
        while (true) {
            coll.find(new Document("country", author))
                    .sort(new Document("date", -1))
                    .limit(1000)
                    .forEach(doNothing);
            reportProgress(1);
            Thread.sleep(10);
        }
    }

}
