package com.liuhuaxin.utli;

import com.liuhuaxin.dao.MongoDBHelper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuanyuan on 2018/4/9.
 */
public class Bilibili2MongoDB {

    private static String FILE_NAME = "用李云龙语音在绝地求生种调戏路人.xml";

    public static void main(String[] args) {
        List<String[]> list = ReadWrite.readFromBilibili(FILE_NAME);
        save(list);
    }

    private static void save(List<String[]> list) {
        MongoDBHelper mongoDBHelper = new MongoDBHelper();
        MongoDatabase connection = mongoDBHelper.getConnection();

        MongoCollection<Document> collection = connection.getCollection("danmu");
        Document titleDoc = new Document();
        titleDoc.append("title", FILE_NAME.split("\\.")[0]);
        List<Document> danmulist = new ArrayList<Document>();
        for (String[] arr : list) {
            Document document = new Document();
            document.append("time", Double.parseDouble(arr[0]));
            document.append("message", regulerDanmu(arr[1]));
            document.append("user", arr[2]);
            danmulist.add(document);
        }
        Collections.sort(danmulist, new Comparator<Document>() {
            public int compare(Document o1, Document o2) {
                return o1.getDouble("time").compareTo(o2.getDouble("time"));
            }
        });
        titleDoc.append("danmu", danmulist);
        collection.insertOne(titleDoc);
        mongoDBHelper.close();
    }

    private static String regulerDanmu(String danmu) {
        String[] regulers = {"2333*", "6666*", "hhhh*"};
        String[] targets = {"233", "666", "hhh"};
        for (int i=0; i<regulers.length; i++) {
            Pattern r = Pattern.compile(regulers[i]);
            Matcher m = r.matcher(danmu);
            danmu = m.replaceAll(targets[i]);
        }
        return danmu;
    }
}
