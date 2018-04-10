package com.liuhuaxin;

import com.liuhuaxin.dao.DBHelper;
import com.liuhuaxin.utli.POIUtil;
import com.liuhuaxin.utli.ReadWrite;
import com.liuhuaxin.wordsplit.WordSplitAnsj_seg;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class Test {
    private static DBHelper dao = new DBHelper();
    private static PreparedStatement ps;
    private static Statement st;
    private static ResultSet rs;
    private static Connection con;
    private static WordSplitAnsj_seg ws = new WordSplitAnsj_seg();

    public static void main(String args[]) {

        ReadWrite readWrite = new ReadWrite();
        List<String> wordList = readWrite.read("积极.txt");
        for (String word : wordList) {
            savaWord("posdict", word);
        }



//        List<String[]> excel;
//        try {
//            excel = POIUtil.readExcel(new File("src/doc", "情感词汇本体.xlsx"));
//            for (String[] row : excel) {
////                if (row[1].equals("1")) {
////                    savaWord("posdict", row[0].toLowerCase());
//////                    System.out.println(row[0].toLowerCase() + " === 积极的");
////                }
////                if (row[1].equals("2")) {
////                    savaWord("negdict", row[0].toLowerCase());
//////                    System.out.println(row[0].toLowerCase() + " === 消极的");
////                }
//                if (row[1].equals("0")) {
//                    System.out.println(row[0]);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }



    /**
     * 保存词语到词库
     * @param word
     */
    private static void savaWord(String table, String word) {
        try {
            con = dao.getCon();
            String sql = "INSERT INTO " + table + " VALUES (?)";
            ps = con.prepareStatement(sql);
            ps.setString(1, word);
            boolean flag = ps.execute();
            if (!flag) {
                System.out.println(word);
            }
            dao.closeAll(rs, ps, con);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void findWord() {
        try {
            con = dao.getCon();
            st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = st.executeQuery("select * from over where word='过劲'");

            if (rs.next()) {
                System.out.println("find");
            } else {
                System.out.println("not find");
            }
            dao.closeAll(rs, st, con);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
