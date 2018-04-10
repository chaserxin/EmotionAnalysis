package com.liuhuaxin.utli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件读写
 */
public class ReadWrite {


    public static List<String> read(String fileName) {
        List<String> result = new ArrayList<String>();
        BufferedReader br = null;
        try {
            File infile = new File("src/doc", fileName);
            br = new BufferedReader(new FileReader(infile));
            String readline = "";
            while ((readline = br.readLine()) != null) {
                result.add(readline);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;

    }

    /**
     * 从 Bilibili 弹幕文件中读取数据
     * 第一行：弹幕在视频中的位置
     * 第二行：弹幕的内容
     * 第三行：弹幕的发送者ID
     * @param fileName
     * @return
     */
    public static List<String[]> readFromBilibili(String fileName) {
        List<String[]> result = new ArrayList<String[]>();
        BufferedReader br = null;
        try {
            File infile = new File("src/doc/bilibili", fileName);
            br = new BufferedReader(new FileReader(infile));
            String readline = "";
            while ((readline = br.readLine()) != null) {
                if (readline.contains("</d>") && readline.contains("<d")) {
                    String[] arr = new String[3];
                    arr[0] = readline.split("\"")[1].split(",")[0];
                    arr[1] = readline.split(">")[1].split("<")[0];
                    arr[2] = readline.split("\"")[1].split(",")[6];
                    result.add(arr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;

    }

    public static void write(String str, String fileName) {
        PrintWriter pw = null;
        try {
            File outfile = new File("src/doc", fileName);
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(outfile, false), "utf-8");//第二个参数意义是说是否以append方式添加内容
            if (!outfile.exists()) {
                outfile.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(osw);
            pw = new PrintWriter(bw, true);
            pw.print(str);
            pw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pw.close();
        }
    }


}
