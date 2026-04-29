package com.smartexam.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = "jdbc:sqlite:smartexam.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver missing: add sqlite-jdbc-*.jar to the classpath.");
        }
    }

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL);
            if (conn == null) {
                throw new java.sql.SQLException("Connection returned null");
            }
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Database Connection Failed: " + e.getMessage(), e);
        }
    }
}
