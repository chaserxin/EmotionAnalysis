package com.liuhuaxin.dao;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by yuanyuan on 2018/4/2.
 */
public class WordDao {

    private DBHelper dao = new DBHelper();

    private PreparedStatement ps;
    private ResultSet rs;
    private Connection con;

	Map<String, Set<String>> tableMap = new HashMap<String, Set<String>>();

	{
		String TableName[] = {"posdict", "negdict", "insufficiently", "ish", "more", "most", "over", "very", "inverse", "idea", "result", "but", "f_and"};
		MongoDatabase mongoDatabase = MongoDBHelper.getConnection();
		for (String table : TableName) {
			MongoCollection<Document> collection = mongoDatabase.getCollection(table);
			MongoCursor<Document> cursor = collection.find(new Document()).iterator();
			Set<String> wordSet = new HashSet<String>();
			while (cursor.hasNext()) {
				Document document = cursor.next();
				String word = document.getString("word");
				wordSet.add(word);
			}
			tableMap.put(table, wordSet);
			cursor.close();
		}
		MongoDBHelper.close();
	}

//    public boolean findWord(String table, String word) {
//        boolean flag = false;
//        con = dao.getCon();
//        String sql = "select * from " + table + " where word = ?";
//        try {
//            ps = con.prepareStatement(sql);
//            ps.setString(1, word);
//            rs = ps.executeQuery();
//            while(rs.next()){
//                flag = true;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            dao.closeAll(rs, ps, con);
//        }
//        return flag;
//    }


    public boolean findWord(String table, String word) {
        boolean flag = false;
		Set<String> wordSet = tableMap.get(table);
		if (wordSet.contains(word)) {
			flag = true;
		}
        return flag;
    }

}
