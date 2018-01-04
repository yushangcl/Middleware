package com.whh.middleware.mysql;

import java.sql.*;

/**
 * Created by huahui.wu on 2018/1/3.
 */
public class MySqlClient {

    public static void main(String[] args) throws Exception {
        Connection conn = null;
        String sql;
        // MySQL的JDBC URL编写方式：jdbc:mysql://主机名称：连接端口/数据库的名称?参数=值
        // 避免中文乱码要指定useUnicode和characterEncoding
        // 执行数据库操作之前要在数据库管理系统上创建一个数据库，名字自己定，
        // 下面语句之前就要先创建javademo数据库
        String url = "jdbc:mysql://123.207.119.211:3306/test?useUnicode=true&characterEncoding=utf-8&useSSL=false";

        try {
            // 之所以要使用下面这条语句，是因为要使用MySQL的驱动，所以我们要把它驱动起来，
            // 可以通过Class.forName把它加载进去，也可以通过初始化来驱动起来，下面三种形式都可以
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            // or:
            // com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
            // or：
            // new com.mysql.jdbc.Driver();

            System.out.println("成功加载MySQL驱动程序");
            // 一个Connection代表一个数据库连接
            Long start = System.currentTimeMillis();
            conn = DriverManager.getConnection(url, "user","123456789..++");
            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
            Long mid = System.currentTimeMillis();
            System.out.println("连接数据库耗时：" + (mid - start) + "ms");

            Statement stmt = conn.createStatement();
            sql = "SELECT NO, name from student";
            ResultSet result = stmt.executeQuery(sql);// executeUpdate语句会返回一个受影响的行数，如果返回-1就没有成功
            Long end = System.currentTimeMillis();
            System.out.println("查询耗时：" + (end - mid) + "ms");
            while (result.next()) {
                System.out.println(result.getString(1) + "\t" + result.getString(2));// 入如果返回的是int类型可以用getInt()
            }
        } catch (SQLException e) {
            System.out.println("MySQL操作错误");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }

    }
}
