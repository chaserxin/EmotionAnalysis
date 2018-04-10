package com.liuhuaxin.dao;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Created by yuanyuan on 2018/4/8.
 */
public class MongoDBHelper {

    static MongoClient mongoClient = null;
    static MongoDatabase mongoDatabase = null;

    public static MongoDatabase getConnection() {

        if (mongoClient == null) {
            // 连接到 mongodb 服务
            mongoClient = new MongoClient( "localhost" , 27017 );
            // 连接到数据库
            mongoDatabase = mongoClient.getDatabase("emotiondict");

        }
        return mongoDatabase;
    }

    public static void close() {
        mongoClient.close();
    }
}
