package com.liuhuaxin.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.*;

/**
 * Created by yuanyuan on 2018/4/9.
 */
public class DanmuDao {

    public List<Map<String, Object>> getDanmu(String title) {
        MongoDatabase mongoDatabase = MongoDBHelper.getConnection();
        MongoCollection<Document> collection = mongoDatabase.getCollection("danmu");
        MongoCursor<Document> cursor = collection.find(new Document("title", title)).iterator();
        Document document = cursor.next();
        List<Map<String, Object>> list = document.get("danmu", List.class);
        return list;
    }
}
