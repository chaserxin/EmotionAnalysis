package com.liuhuaxin.analysis;

import com.liuhuaxin.bean.Sentence;
import com.liuhuaxin.dao.DBHelper;
import com.liuhuaxin.dao.WordDao;
import com.liuhuaxin.utli.ReadWrite;

import java.sql.*;

/**
 * 简单句情感分析
 */
public class Analyse {
    public static String rc = "";

    private static WordDao wordDao = new WordDao();

    private static boolean get = false;//控制是否搜索到词语
    private static boolean moodkind = false;//判断该词是否已经判定为正或负情感词
    private static boolean degreekind = false;//判断程度词是否已经确定

    private static String TableName[] = {"posdict", "negdict", "insufficiently", "ish", "more", "most", "over", "very", "inverse"};



    /**
     * 单个词中文搜索
     */
    public static void find(String table, String word) {
        if (wordDao.findWord(table, word)) {
            //                System.out.println("*****************" + word + " 在：：：" + table + "中");
            rc += "*****************" + word + " 在：：：" + table + "中" + "\r\n";
            get = true;//找到词修改get
        } else {
//            System.out.println(table + "这个表里面没有:" + word);
            rc += table + "这个表里面没有:" + word + "\r\n";
        }
    }

    /**
     * 负责特征词搜索的方法
     */
    public static boolean hasfeature(String table, String word) {
        return wordDao.findWord(table, word);
    }

    /**
     *  句子分值计算
     */
    public static void calculate(Sentence sen) {
        int lastmoodword = -1;//记录上一个情感词的位置

//        System.out.println("当前句子keyPart为：" + sen.getKeyPart());
        rc += "当前句子keyPart为：" + sen.getKeyPart() + "\r\n";

        for (int j = 0; j < sen.parts.size(); j++) {//在情感词典里搜索情感词,j是单个词的索引
            for (int i = 0; i <= 1 && !moodkind; i++) {//i是表索引,先在正负情感词表里开始搜索,且每次循环判断是情感否词性已定，避免重复搜索
                find(TableName[i], sen.parts.get(j));
                if (get) { //如果搜到情感词
                    get = false;
                    moodkind = true;//搜到则词性已定


                    if (TableName[i].equals("posdict")) {
                        sen.setT(1);//在积极情感词典中,正情感分值加一

//                        System.out.println("积极分+1");
                        rc += "积极分+1" + "\r\n";

//                        System.out.println("*********开始搜索积极情感词前的程度词***********");
                        rc += "*********开始搜索积极情感词前的程度词***********" + "\r\n";
                    } else if (TableName[i].equals("negdict")) {
                        sen.setT(-1);//在消极情感词典中

//                        System.out.println("消极分+1");
                        rc += "消极分+1" + "\r\n";

//                        System.out.println("*********开始搜索消极情感词前的程度词***********");
                        rc += "*********开始搜索消极情感词前的程度词***********" + "\r\n";
                    }
                        /*
						 * 搜索程度词和否定词
						 * */
                    for (int m = lastmoodword + 1; m < j; m++) {//m是情感词前的单个词的索引
                        find(TableName[TableName.length - 1], sen.parts.get(m));//在inverse中查找否定词
                        if (get) {
                            get = false;
                            sen.setN(sen.getN() + 1);//否定词个数加1
                            sen.setpN(m);
                        }

                        for (int k = 2; k < TableName.length - 1 && !degreekind; k++) {//k是表索引,在两情感词之间寻找程度词
                            find(TableName[k], sen.parts.get(m));
                            if (get) {
                                get = false;
                                degreekind = true;
                                sen.setpM(m);
                                modify(sen, TableName[k]);//设置程度分值
                            }
                        }
                        degreekind = false;
                    }
                    lastmoodword = j;
                }
            }
            moodkind = false;
        }

//        System.out.println(sen.getPosition() + "句否定词个数为：" + sen.getN());
        rc += sen.getPosition() + "句否定词个数为：" + sen.getN() + "\r\n";

//        System.out.println(sen.getPosition() + "句程度词分为：" + sen.getM());
        rc += sen.getPosition() + "句程度词分为：" + sen.getM() + "\r\n";

//        System.out.println(sen.getPosition() + "句情感词分为：" + sen.getT());
        rc += sen.getPosition() + "句情感词分为：" + sen.getT() + "\r\n" + "\r\n";

        lastmoodword = -1;//每搜索完一句话就把上一情感词位置清零
        ReadWrite.write(rc, "analyserecording.txt");//写入分析过程
    }


    /*
     * 对程度词分值进行修正
     * */
    private static void modify(Sentence sen, String table) {
        boolean decrease = false;

        if (table.equals("insufficiently")) {
            sen.setM(0.25);
            decrease = true;

//            System.out.println("程度分x0.25");
            rc += "程度分x0.25" + "\r\n";

        } else if (table.equals("ish")) {
            sen.setM(0.5);
            decrease = true;

//            System.out.println("程度分x0.5");
            rc += "程度分x0.5" + "\r\n";

        } else if (table.equals("more")) {
            sen.setM(1.25);

//            System.out.println("程度分x1.25");
            rc += "程度分x1.25" + "\r\n";

        } else if (table.equals("most")) {
            sen.setM(2.0);

//            System.out.println("程度分x2.0");
            rc += "程度分x2.0" + "\r\n";

        } else if (table.equals("over")) {
            sen.setM(1.35);

//            System.out.println("程度分x1.25");
            rc += "程度分x1.25" + "\r\n";

        } else if (table.equals("very")) {
            sen.setM(1.5);

//            System.out.println("程度分x1.5");
            rc += "程度分x1.5" + "\r\n";

        }

        if (!decrease && sen.getpN() < sen.getpM() && sen.getM() > 0 && sen.getN() > 0) {//否定词+增强型程度词+情感词 类型
            sen.setP(0.5);
            sen.setN(sen.getN() - 1);

//            System.out.println("句式分为：" + sen.getP());
            rc += "句式分为：" + sen.getP();

        }
    }
}