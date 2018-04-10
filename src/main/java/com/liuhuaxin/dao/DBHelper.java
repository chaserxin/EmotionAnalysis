package com.liuhuaxin.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 连接数据库
 */
public class DBHelper {
    //mysql驱动
    private final static String DRIVER = "com.mysql.jdbc.Driver";
    //URL
    private final static String URL = "jdbc:mysql://127.0.0.1:3306/moodsdic?useSSL=false";
    //MySql账号，密码
    private final static String USER = "root";
    private final static String PWD = "";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("驱动加载失败");
        }
    }

    public static Connection getCon(){
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USER, PWD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    public static void closeAll(ResultSet rs, Statement st, Connection con){
        try {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}