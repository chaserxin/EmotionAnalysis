package com.liuhuaxin.utli;

import com.liuhuaxin.dao.DBHelper;
import com.liuhuaxin.dao.MongoDBHelper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanyuan on 2018/4/8.
 */
public class Mysql2MongoDB {

    private DBHelper dao = new DBHelper();

    private PreparedStatement ps;
    private ResultSet rs;
    private Connection con;


    private static String TableName[] = {"posdict", "negdict", "insufficiently", "ish", "more", "most", "over", "very", "inverse", "idea", "result", "but", "f_and"};


    private List<Document> findWord(String table) {
        List<Document> documents = new ArrayList<Document>();
        con = dao.getCon();
        String sql = "select * from " + table;
        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){
                System.out.println(rs.getString(1));
                Document document = new Document("word", rs.getString(1));
                documents.add(document);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dao.closeAll(rs, ps, con);
        }
        return documents;
    }


    public static void main(String[] args) {
        Mysql2MongoDB main = new Mysql2MongoDB();

        MongoDBHelper mongoDBHelper = new MongoDBHelper();
        MongoDatabase connection = mongoDBHelper.getConnection();

        for (String table : TableName) {
            MongoCollection<Document> collection = connection.getCollection(table);
            List<Document> word = main.findWord(table);
            collection.insertMany(word);
        }
        mongoDBHelper.close();
    }
}
